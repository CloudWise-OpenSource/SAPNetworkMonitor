package cli

import (
	"log"
	"fmt"
	"strconv"
	"encoding/json"
	"net/http"
	"bytes"
	"SAPNetworkMonitor/monitor/src/models"
	"container/list"
)

var (
	stabilityFlag bool
	timeoutFlag bool
)

func init() {
	stabilityFlag = true
	timeoutFlag = true
}

func DelayAndBrandwidth(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	taskMap map[string] *list.List,osm map[string] string) {
	log.Println("Start DelayAndBrandwidth")
	fmt.Println("@@@@@@@@@" + strconv.Itoa(monitorJob.Data.Interval))  // job没有变
	fmt.Println("{{{{{{{{{{{" + strconv.Itoa(len(taskMap)))
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		channel_result1 := make(chan models.MonitorResult,1)
		channel_result2 := make(chan models.MonitorResult,1)
		go	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,0,0,channel_result1,osm)
		go	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.RoundTripTimeB, monitorJob.Data.JobDesc.RoundTripTimeL,0,1,channel_result2,osm)
		list_a,pid_a := FindNipingPid(monitorJob.Data.TaskId,serverInfo,taskMap)
		list_b,pid_b := FindNipingPid(monitorJob.Data.TaskId,serverInfo,taskMap)
		if list_a.Len() != 0 {
			DeleteNipingPid(list_a,pid_a)
		}
		if list_b.Len() != 0 {
			DeleteNipingPid(list_b,pid_b)
		}
		monitorResult_a := <- channel_result1
		monitorResult_b := <- channel_result2

		monitorResult := models.MonitorResult{monitorResult_a.Av2,monitorResult_a.Avg,monitorResult_a.EndTime,monitorResult_a.Errmsg,
											 monitorResult_a.Errno,monitorResult_a.Max,monitorResult_a.Min,monitorResult_a.StartTime,monitorResult_a.TaskId,
											 monitorResult_b.Tr,monitorResult_b.Tr2,0}
		monitorResultJson, _ := json.Marshal(monitorResult)
		req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
		fmt.Println(req)
		if err != nil {
			fmt.Println("Error:", err)
		}
		req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
		req.Header.Set("Content-Type","application/json")
		client := &http.Client{}
		resp, err1 := client.Do(req)
		if err1 != nil {
			log.Println("annot get response")
		}else {
			fmt.Println(resp)
		}
	}
}

//稳定性
func StabilityTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	taskMap map[string] *list.List,osm map[string] string){
	if stabilityFlag == true{
		log.Println("Start StabilityTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			stabilityFlag = false
			channel_result := make(chan models.MonitorResult,1)
			go NipingCMD(1,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"], monitorJob.Data.JobDesc.StabilityB,
				monitorJob.Data.JobDesc.StabilityL,monitorJob.Data.JobDesc.StabilityD,2,channel_result,osm)
			list,pid := FindNipingPid(monitorJob.Data.TaskId,serverInfo,taskMap)
			monitorResult_stab := <- channel_result
			if list.Len() != 0 {
				DeleteNipingPid(list,pid)
			}
			monitorResult := models.MonitorResult{monitorResult_stab.Av2,monitorResult_stab.Avg,monitorResult_stab.EndTime,monitorResult_stab.Errmsg,
												 monitorResult_stab.Errno,monitorResult_stab.Max,monitorResult_stab.Min,monitorResult_stab.StartTime,monitorResult_stab.TaskId,
												 monitorResult_stab.Tr,monitorResult_stab.Tr2,1}
			fmt.Println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
			fmt.Println(monitorResult)
			monitorResultJson, _ := json.Marshal(monitorResult)
			req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
			if err != nil {
				fmt.Println("Error:", err)
			}
			req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
			req.Header.Set("Content-Type","application/json")
			client := &http.Client{}
			stabilityFlag = true
			resp, err1 := client.Do(req)
			if err1 != nil {
				log.Println("cannot get response")
			}else {
				if resp.StatusCode == 200 {
					fmt.Println(resp)
				}
			}
		}
	}
}
//闲置超时
func TimeoutTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	taskMap map[string] *list.List,osm map[string] string){
	if timeoutFlag == true{
		log.Println("Start TimeoutTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			timeoutFlag = false
			channel_result := make(chan models.MonitorResult,1)
			go NipingCMD(2,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
				monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,monitorJob.Data.JobDesc.IdleTimeoutD,2,channel_result,osm)
			list,pid := FindNipingPid(monitorJob.Data.TaskId,serverInfo,taskMap)
			monitorResult_idle := <- channel_result
			if list.Len() != 0 {
				DeleteNipingPid(list,pid)
			}
			monitorResult := models.MonitorResult{monitorResult_idle.Av2,monitorResult_idle.Avg,monitorResult_idle.EndTime,monitorResult_idle.Errmsg,
												 monitorResult_idle.Errno,monitorResult_idle.Max,monitorResult_idle.Min,monitorResult_idle.StartTime,monitorResult_idle.TaskId,
												 monitorResult_idle.Tr,monitorResult_idle.Tr2,2}
			fmt.Println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
			fmt.Println(monitorResult)
			monitorResultJson, _ := json.Marshal(monitorResult)
			req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
			if err != nil {
				fmt.Println("Error:", err)
			}
			req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
			req.Header.Set("Content-Type","application/json")
			client := &http.Client{}
			timeoutFlag = true
			resp, err1 := client.Do(req)
			if err1 != nil {
				log.Println("cannot get response")
			}else {
				if resp.StatusCode == 200 {
					fmt.Println(resp)
				}
			}
		}
	}
}
