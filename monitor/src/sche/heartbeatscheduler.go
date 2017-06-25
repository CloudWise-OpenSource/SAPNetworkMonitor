package sche

import (
	"encoding/json"
	"fmt"
	"log"
	"SAPNetworkMonitor/monitor/src/cli"
	"net/http"
	"bytes"
	"github.com/jasonlvhit/gocron"
	"SAPNetworkMonitor/monitor/src/models"
	oss "os"
)

var (
	taskMap = make(map[string] string)
)

func HeartBeat(url string,nipingtInterval int64,serverInfo map[string] string,monitorInfo map[string] string){
	log.Println("Start Heartbeat")
	nipingT,errFlag := cli.GetNipingT(serverInfo,nipingtInterval)
	heartbeats := models.HeartBeats{
		Ip:			monitorInfo["ip"],
		Name:		monitorInfo["name"],
		Country:	monitorInfo["country"],
		Area:		monitorInfo["area"],
		Province:	monitorInfo["province"],
		City:		monitorInfo["city"],
		Isp:		monitorInfo["isp"],
		MonitorId:	monitorInfo["monitorId"],
		NipingT:	nipingT,
		RunningTaskIds:	GetTaskIds(),
	}
	jsons, _ := json.Marshal(heartbeats)
	monitorJob := new(models.MonitorJob)
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsons))
	if err != nil {
		fmt.Println("Error:", err)
	}
	req.Header.Set("Authorization","Bearer " +  monitorInfo["accessToken"])
	req.Header.Set("Content-Type","application/json")
	fmt.Println(req)
	client := &http.Client{}
	resp,err1 :=client.Do(req)
	if errFlag == true {
		oss.Exit(1)
	}
	if err1 != nil {
		log.Println("cannot get the response")
		gocron.Clear()
		oss.Exit(1)
	}
	log.Println("received response")
	buf := new(bytes.Buffer)
	buf.ReadFrom(resp.Body)
	s := buf.String()
	json.Unmarshal([]byte(s), &monitorJob)

	fmt.Println("###################" + s)
	fmt.Println("&&&&&&&&&&&    " + monitorJob.Data.MonitorId)
	if monitorJob.Data.MonitorId == monitorInfo["monitorId"] {
		switch monitorJob.Data.ActionType {
		case 0:
			log.Print("start task0")
			StopTask(monitorJob.Data.TaskId, taskMap)
			log.Println("stop task:" +  monitorJob.Data.TaskId)
			break
		case 1:
			log.Print("start task1")
			taskMap[monitorJob.Data.TaskId] = ""
			StartJob(*monitorJob,serverInfo,monitorInfo,taskMap)
			break
		case 2:
			log.Print("start task2")
			StopTask(monitorJob.Data.TaskId, taskMap)
			log.Println("stop task:" +  monitorJob.Data.TaskId)
			StartJob(*monitorJob,serverInfo,monitorInfo,taskMap)
			break
		}
	}
}

func GetTaskIds() []string {
	taskIds := append([]string{})
	for taskId,_ := range taskMap {
		taskIds = append(taskIds,taskId)
	}
	return taskIds
}
