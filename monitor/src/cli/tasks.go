package cli

import (
	"log"
	"encoding/json"
	"net/http"
	"bytes"
	"SAPNetworkMonitor/monitor/src/models"
	"time"
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
	taskMap map[string] string) {
	log.Println("Start DelayAndBrandwidth")
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		startTime := time.Now().Unix()
		monitorResult_a :=	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.RoundTripTimeB, monitorJob.Data.JobDesc.RoundTripTimeL,0,0)
		monitorResult_b :=	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,0,1)
		endTime := time.Now().Unix()
		monitorResult := models.MonitorResult{monitorResult_a.Av2,monitorResult_a.Avg,endTime,monitorResult_a.Errmsg,
											 monitorResult_a.Errno,monitorResult_a.Max,monitorResult_a.Min,startTime,monitorResult_a.TaskId,
											 monitorResult_b.Tr,monitorResult_b.Tr2,0,monitorJob.Data.MonitorId}
		log.Println(monitorResult)
		monitorResultJson, _ := json.Marshal(monitorResult)
		req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
		if err != nil {
			log.Println("Error:", err)
		}
		req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
		req.Header.Set("Content-Type","application/json")
		client := &http.Client{}
		resp, err1 := client.Do(req)
		if err1 != nil {
			log.Println("DelayAndBrandwidth Cannot Get Response")
			log.Println("The possible problem is the wrong dataServerUrl: " +  serverInfo["dataServerUrl"])
		}else {
			log.Println(resp)
		}
	}else {
		log.Println("No Valid ask")
	}
}

//稳定性
func StabilityTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	taskMap map[string] string){
	if stabilityFlag == true{
		log.Println("Start StabilityTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			stabilityFlag = false
			monitorResult_stab := NipingCMD(1,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"], monitorJob.Data.JobDesc.StabilityB,
				monitorJob.Data.JobDesc.StabilityL,monitorJob.Data.JobDesc.StabilityD,2)
			monitorResult := models.MonitorResult{monitorResult_stab.Av2,monitorResult_stab.Avg,monitorResult_stab.EndTime,monitorResult_stab.Errmsg,
												 monitorResult_stab.Errno,monitorResult_stab.Max,monitorResult_stab.Min,monitorResult_stab.StartTime,monitorResult_stab.TaskId,
												 monitorResult_stab.Tr,monitorResult_stab.Tr2,1,monitorJob.Data.MonitorId}
			log.Println(monitorResult)
			monitorResultJson, _ := json.Marshal(monitorResult)
			req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
			if err != nil {
				log.Println("Error:", err)
			}
			req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
			req.Header.Set("Content-Type","application/json")
			client := &http.Client{}
			stabilityFlag = true
			resp, err1 := client.Do(req)
			if err1 != nil {
				log.Println("StabilityTask Cannot Get Response")
				log.Println("The possible problem is the wrong dataServerUrl: " +  serverInfo["dataServerUrl"])
			}else{
				log.Println(resp)
			}
		}else {
			log.Println("No Valid Task")
		}
	}
}
//闲置超时
func TimeoutTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	taskMap map[string] string){
	if timeoutFlag == true{
		log.Println("Start TimeoutTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			timeoutFlag = false
			startTime := time.Now().Unix()
			monitorResult_idle := NipingCMD(2,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
				monitorJob.Data.JobDesc.BandwithB,1,monitorJob.Data.JobDesc.IdleTimeoutD,2)
			endTime := time.Now().Unix()
			monitorResult := models.MonitorResult{monitorResult_idle.Av2,monitorResult_idle.Avg,endTime,monitorResult_idle.Errmsg,
												 monitorResult_idle.Errno,monitorResult_idle.Max,monitorResult_idle.Min,startTime,monitorResult_idle.TaskId,
												 monitorResult_idle.Tr,monitorResult_idle.Tr2,2,monitorJob.Data.MonitorId}
			log.Println(monitorResult)
			monitorResultJson, _ := json.Marshal(monitorResult)
			req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
			if err != nil {
				log.Println("Error:", err)
			}
			req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
			req.Header.Set("Content-Type","application/json")
			client := &http.Client{}
			timeoutFlag = true
			resp, err1 := client.Do(req)
			if err1 != nil {
				log.Println("TimeoutTask Cannot Get Response")
				log.Println("The possible problem is the wrong dataServerUrl: " +  serverInfo["dataServerUrl"])
			}else {
				log.Println(resp)
			}
		}else {
			log.Println("No Valid Task")
		}
	}
}
