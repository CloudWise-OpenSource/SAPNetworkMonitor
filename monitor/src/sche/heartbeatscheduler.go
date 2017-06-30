package sche

import (
	"encoding/json"
	"fmt"
	"log"
	"SAPNetworkMonitor/monitor/src/cli"
	"net/http"
	"bytes"
	"SAPNetworkMonitor/monitor/src/models"
)

var (
	taskMap = make(map[string] string)
)

func HeartBeat(nipingtInterval int64,serverInfo map[string] string,monitorInfo map[string] string){
	log.Println("Start Heartbeat")
	nipingT := cli.GetNipingT(serverInfo,nipingtInterval)
	monitorId := cli.GetMonitorId()
	url := serverInfo["heartbeatServerUrl"] + "/api/monitors/monitor/" + monitorId + "/heartbeat"
	heartbeats := models.HeartBeats{
		Ip:			monitorInfo["ip"],
		Name:		monitorInfo["name"],
		Country:	monitorInfo["country"],
		Area:		monitorInfo["area"],
		Province:	monitorInfo["province"],
		City:		monitorInfo["city"],
		Isp:		monitorInfo["isp"],
		MonitorId:	monitorId,
		NipingT:	nipingT,
		RunningTaskIds:	GetTaskIds(),
		Version:	"0.0.1",
	}
	jsons, _ := json.Marshal(heartbeats)
	monitorJob := new(models.MonitorJob)
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsons))
	if err != nil {
		fmt.Println("Error:", err)
	}
	req.Header.Set("Authorization","Bearer " +  monitorInfo["accessToken"])
	req.Header.Set("Content-Type","application/json")
	log.Println(req)
	client := &http.Client{}
	resp,err1 :=client.Do(req)
	if err1 != nil {
		log.Println("Cannot Get the Response")
		log.Println("The possible problem is the wrong heartbeatServerUrl: " +  serverInfo["heartbeatServerUrl"])
	}else if resp.StatusCode == 200 {
		log.Println("Received Response")
		buf := new(bytes.Buffer)
		buf.ReadFrom(resp.Body)
		s := buf.String()
		json.Unmarshal([]byte(s), &monitorJob)
		if monitorJob.Data.MonitorId != "" {
			switch monitorJob.Data.ActionType {
			case 0:
				log.Print("Start Task0")
				StopTask(monitorJob.Data.TaskId, taskMap)
				log.Println("Stop Task:" +  monitorJob.Data.TaskId)
				break
			case 1:
				log.Print("Start Task1")
				taskMap[monitorJob.Data.TaskId] = ""
				StartJob(*monitorJob,serverInfo,taskMap)
				break
			case 2:
				log.Print("Start Task2")
				StopTask(monitorJob.Data.TaskId, taskMap)
				log.Println("Stop Task:" +  monitorJob.Data.TaskId)
				StartJob(*monitorJob,serverInfo,taskMap)
				break
			}
		}
	}else {
		log.Println(resp)
	}
}

func GetTaskIds() []string {
	taskIds := append([]string{})
	for taskId,_ := range taskMap {
		taskIds = append(taskIds,taskId)
	}
	return taskIds
}
