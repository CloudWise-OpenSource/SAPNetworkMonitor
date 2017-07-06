package cli

import (
	"log"
	"encoding/json"
	"net/http"
	"bytes"
	"SAPNetworkMonitor/monitor/src/models"
	"time"
	"net"
)

//时延和带宽
func DelayAndBrandwidth(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	monitorInfo map[string] string,taskMap map[string] string,errno int) {
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		log.Println("Start DelayAndBrandwidth: " + monitorJob.Data.TaskId)
		startTime := time.Now().Unix()
		monitorResult_a :=	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.RoundTripTimeB, monitorJob.Data.JobDesc.RoundTripTimeL,0,0,errno)
		monitorResult_b :=	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,0,1,errno)
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
		req.Header.Set("Authorization","Bearer " + monitorInfo["accessToken"])
		req.Header.Set("Content-Type","application/json")
		client := &http.Client{
			Transport: &http.Transport{
				Dial: func(netw, addr string) (net.Conn, error) {
					c, err := net.DialTimeout(netw, addr, time.Second*10) //设置建立连接超时
					if err != nil {
						return nil, err
					}
					c.SetDeadline(time.Now().Add(30 * time.Second)) //设置发送接收数据超时
					return c, nil
				},
			},
		}
		resp, err1 := client.Do(req)
		if err1 != nil {
			log.Println("DelayAndBrandwidth Cannot Get Response")
			log.Println("The possible problem may be the dataServerUrl: " +  serverInfo["dataServerUrl"] + " or the network environment")
		}else {
			log.Println(resp)
		}
	}else {
		log.Println("No Valid ask")
	}
}

//稳定性
func StabilityTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	monitorInfo map[string] string,taskMap map[string] string,errno int){
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		log.Println("Start StabilityTask: "+ monitorJob.Data.TaskId)
		monitorResult_stab := NipingCMD(1,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"], monitorJob.Data.JobDesc.StabilityB,
			monitorJob.Data.JobDesc.StabilityL,monitorJob.Data.JobDesc.StabilityD,2,errno)
		monitorResult := models.MonitorResult{monitorResult_stab.Av2,monitorResult_stab.Avg,monitorResult_stab.EndTime,monitorResult_stab.Errmsg,
											  monitorResult_stab.Errno,monitorResult_stab.Max,monitorResult_stab.Min,monitorResult_stab.StartTime,monitorResult_stab.TaskId,
											  monitorResult_stab.Tr,monitorResult_stab.Tr2,1,monitorJob.Data.MonitorId}
		log.Println(monitorResult)
		monitorResultJson, _ := json.Marshal(monitorResult)
		req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
		if err != nil {
			log.Println("Error:", err)
		}
		req.Header.Set("Authorization","Bearer " + monitorInfo["accessToken"])
		req.Header.Set("Content-Type","application/json")
		client := &http.Client{
			Transport: &http.Transport{
				Dial: func(netw, addr string) (net.Conn, error) {
					c, err := net.DialTimeout(netw, addr, time.Second*10) //设置建立连接超时
					if err != nil {
						return nil, err
					}
					c.SetDeadline(time.Now().Add(30 * time.Second)) //设置发送接收数据超时
					return c, nil
				},
			},
		}
		resp, err1 := client.Do(req)
		if err1 != nil {
			log.Println("StabilityTask Cannot Get Response")
			log.Println("The possible problem may be the dataServerUrl: " +  serverInfo["dataServerUrl"]+ " or the network environment")
		}else{
			log.Println(resp)
		}
	}else {
		log.Println("No Valid Task")
	}
}
//闲置超时
func TimeoutTask(url string,monitorJob models.MonitorJob,serverInfo map[string] string,
	monitorInfo map[string] string,taskMap map[string] string,errno int){
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		log.Println("Start TimeoutTask: "+ monitorJob.Data.TaskId)
		startTime := time.Now().Unix()
		monitorResult_idle := NipingCMD(2,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			200,1,monitorJob.Data.JobDesc.IdleTimeoutD,2,errno)
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
		req.Header.Set("Authorization","Bearer " + monitorInfo["accessToken"])
		req.Header.Set("Content-Type","application/json")
		client := &http.Client{
			Transport: &http.Transport{
				Dial: func(netw, addr string) (net.Conn, error) {
					c, err := net.DialTimeout(netw, addr, time.Second*10) //设置建立连接超时
					if err != nil {
						return nil, err
					}
					c.SetDeadline(time.Now().Add(30 * time.Second)) //设置发送接收数据超时
					return c, nil
				},
			},
		}
		resp, err1 := client.Do(req)
		if err1 != nil {
			log.Println("TimeoutTask Cannot Get Response")
			log.Println("The possible problem may be the dataServerUrl: " +  serverInfo["dataServerUrl"]+ " or the network environment")
		}else {
			log.Println(resp)
		}
	}else {
		log.Println("No Valid Task")
	}
}
