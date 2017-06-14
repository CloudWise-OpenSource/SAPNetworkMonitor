package main

import (
	"flag"
	"log"
	"fmt"
	"github.com/larspensjo/config"
	"encoding/json"
	"os/exec"
	"net/http"
	"time"
	"io/ioutil"
	"strings"
	"regexp"
	"container/list"
	"strconv"
	"bytes"
	"runtime"
)

var (
	configFile = flag.String("configfile", "config.ini", "General configuration file")
	heartbeat = make(map[string]string)
	monitorConfig = make(map[string]string)
	taskMap = make(map[string] *list.List)
	monitorResult MonitorResult
	monitorJob MonitorJob
	lastSendTime int64
	lastHeartBeat int64
	lastNipingT int64
)

type HeartBeats struct {
	Ip		string		`json:"ip"`
	Name		string		`json:"name"`
	Country		string		`json:"country"`
	Area		string		`json:"area"`
	Province	string		`json:"province"`
	City		string		`json:"city"`
	Isp		string		`json:"isp"`
	MonitorId	string		`json:"monitorId"`
	NipingT		string		`json:"nipingT"`
	RunningTaskIds	[]string	`json:"runningTaskIds"`
}

type MonitorJob struct {
	Code          	int64 		`json:"code"`
	Message		string		`json:"msg"`
	Data       	struct {
		MonitorId	string		`json:"monitorId"`
		TaskId		string		`json:"taskId"`
		Interval	int		`json:"interval"`
		ActionType	int		`json:"actionType"`
		JobDesc		struct {
			Router		string		`json:"router"`
			RoundTripTimeB	int		`json:"roundTripTimeB"`
			RoundTripTimeL	int		`json:"roundTripTimeL"`
			BandwithB	int		`json:"bandwithB"`
			BandwithL	int		`json:"bandwithL"`
			StabilityB	int		`json:"stabilityB"`
			StabilityD	int		`json:"stabilityD"`
			StabilityL	int		`json:"stabilityL"`
			IdleTimeoutD	int		`json:"idleTimeoutD"`
		}	`json:"jobDesc"`
		ModifiedTime	int64		`json:"modifiedTime"`
	}	`json:"data"`
}

type MonitorResult struct {
	Av2      		string        	`json:"av2"`
	Avg			string		`json:"avg"`
	EndTime       		int64        	`json:"endTime"`
	Errmsg      		string 	    	`json:"errmsg"`
	Errno          		string	    	`json:"errno"`
	Max            		string	    	`json:"max"`
	Min      		string        	`json:"min"`
	StartTime		int64		`json:"startTime"`
	TaskId			string		`json:"taskId"`
	Tr  			string	    	`json:"tr"`
	Tr2			string	    	`json:"tr2"`
	Type			int		`json:"type"`
}

func init(){
	av2 := ""
	avg := ""
	endTime := int64(0)
	errmsg := ""
	errno := ""
	max := ""
	min := ""
	startTime := int64(0)
	taskId := ""
	tr := ""
	tr2 := ""
	typeId := 0
	lastSendTime = 0
	lastHeartBeat = 0
	monitorResult = MonitorResult{av2,avg,endTime,errmsg,errno,max,
				      min,startTime, taskId,tr,tr2,typeId}
}

func getTaskIds() []string {
	taskIds := append([]string{})
	for taskId,_ := range taskMap {
		taskIds = append(taskIds,taskId)
	}
	return taskIds
}

func GetNipingT(nipingaddr string,nipingTRate int64) string {
	if time.Now().Unix() - lastNipingT > nipingTRate {
		cmd,err := exec.Command("cmd","/C",nipingaddr +"niping","-t").Output()
		if err != nil {
			fmt.Println(err.Error())
		}
		nipingT := string(cmd)
		return nipingT
	}else {
		nipingT := ""
		return nipingT
	}
}

func readConfig() {
	cfg, err := config.ReadDefault(*configFile)
	if err != nil {
		log.Fatalf("Fail to find", *configFile, err)
	}
	if cfg.HasSection("heartbeat") {
		section, err := cfg.SectionOptions("heartbeat")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("heartbeat", v)
				if err == nil {
					heartbeat[v] = options
				}
			}
		}
	}
	if cfg.HasSection("monitorConfig") {
		section, err := cfg.SectionOptions("monitorConfig")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("monitorConfig", v)
				if err == nil {
					monitorConfig[v] = options
				}
			}
		}
	}
}

func TaskProducer1(jobQueue chan<- http.Response,url string,monitorJob MonitorJob,channel chan int) {
	taskMap[monitorJob.Data.TaskId] = list.New()
	for true {
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			timeSpace := time.Now().Unix() - lastSendTime
			if timeSpace > int64(monitorJob.Data.Interval) {
				channel_result := make(chan MonitorResult,1)
				go NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,heartbeat["nipingaddr"],
					monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,0,0,channel_result)
				list_a,pid_a := findNipingPid(monitorJob.Data.TaskId)
				monitorResult_a  := <- channel_result

				deleteNipingPid(list_a,pid_a)
				go NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,heartbeat["nipingaddr"],
					monitorJob.Data.JobDesc.RoundTripTimeB, monitorJob.Data.JobDesc.RoundTripTimeL,0,1,channel_result)
				list_b,pid_b := findNipingPid(monitorJob.Data.TaskId)
				monitorResult_b := <- channel_result

				deleteNipingPid(list_b,pid_b)
				close(channel_result)
				monitorResult = MonitorResult{monitorResult_a.Av2,monitorResult_a.Avg,monitorResult_a.EndTime,monitorResult_a.Errmsg,
							      monitorResult_a.Errno,monitorResult_a.Max,monitorResult_a.Min,monitorResult_a.StartTime,monitorResult_a.TaskId,
							      monitorResult_b.Tr,monitorResult_b.Tr2,0}

				monitorResultJson, _ := json.Marshal(monitorResult)
				req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
				if err != nil {
					fmt.Println("Error:", err)
				}
				client := &http.Client{}
				resp, err1 := client.Do(req)
				if err1 != nil {
					log.Println("cannot get response")
				}else {
					jobQueue <- *resp
				}
				lastSendTime = time.Now().Unix()
			}
		}else {
			channel <- 1
			break
		}
	}
}

func TaskConsumer1(jobQueue <-chan http.Response) {
	for true{
		resp := <- jobQueue
		if resp.StatusCode == 200 {
			body, _ := ioutil.ReadAll(resp.Body)
			fmt.Println(body)
		}
	}
}

func TaskProducer2(jobQueue chan<- http.Response,url string,monitorJob MonitorJob,channel chan int){
	for true {
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			channel_result := make(chan MonitorResult,1)
			go NipingCMD(1,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,heartbeat["nipingaddr"],monitorJob.Data.JobDesc.StabilityB,monitorJob.Data.JobDesc.StabilityL,monitorJob.Data.JobDesc.StabilityD,2,channel_result)
			list,pid := findNipingPid(monitorJob.Data.TaskId)
			monitorResult_stab := <- channel_result
			deleteNipingPid(list,pid)
			monitorResult = MonitorResult{monitorResult_stab.Av2,monitorResult_stab.Avg,monitorResult_stab.EndTime,monitorResult_stab.Errmsg,
						      monitorResult_stab.Errno,monitorResult_stab.Max,monitorResult_stab.Min,monitorResult_stab.StartTime,monitorResult_stab.TaskId,
						      monitorResult_stab.Tr,monitorResult_stab.Tr2,1}
			lastSendTime = time.Now().Unix()
			monitorResultJson, _ := json.Marshal(monitorResult)
			req, err := http.NewRequest("POST", url, bytes.NewBuffer(monitorResultJson))
			if err != nil {
				fmt.Println("Error:", err)
			}
			client := &http.Client{}
			resp, err1 := client.Do(req)
			if err1 != nil {
				log.Println("cannot get response")
				channel <- 1
			}else {
				jobQueue <- *resp
			}
		}
	}
}

func TaskConsumer2(jobQueue <-chan http.Response){
	resp := <- jobQueue
	if resp.StatusCode == 200 {
		body, _ := ioutil.ReadAll(resp.Body)
		fmt.Println(body)
	}
}

func startJob(monitorJob MonitorJob) {
	url := "http://10.0.5.229:8080/api/databus/monitor/" + heartbeat["monitorId"] + "/result"
	channel1 := make(chan int,1)
	channel2 := make(chan int,1)
	go func() {
		jobQueue1 := make(chan http.Response, 1)
		go TaskProducer1(jobQueue1,url,monitorJob,channel1)
		go TaskConsumer1(jobQueue1)

	}()
	<- channel1
	close(channel1)
	go func() {
		jobQueue2 := make(chan http.Response, 1)
		go TaskProducer2(jobQueue2,url,monitorJob,channel2)
		go TaskConsumer2(jobQueue2)

	}()

	<- channel2

	close(channel2)
}

func SendHeartBeat() {
	readConfig()
	url := "http://10.0.5.229:8080/api/monitors/monitor/" + heartbeat["monitorId"] + "/heartbeat"
	heartbeatRate,_ := strconv.ParseInt(monitorConfig["heartbeatrate"],10,64)
	nipingTRate,_ := strconv.ParseInt(monitorConfig["nipingtate"],10,64)
	runtime.GOMAXPROCS(20)
	queue := make(chan http.Response, 1)
	go Producer(queue,url,heartbeatRate,nipingTRate)
	go Consumer(queue)
	time.Sleep(time.Hour * 1000000)
}

func Producer(queue chan<- http.Response,url string,heartbeatRate int64,nipingTRate int64){
	for true {
		if time.Now().Unix() - lastHeartBeat > heartbeatRate {
			nipingT := GetNipingT(monitorConfig["nipingaddr"],nipingTRate)
			heartbeats := HeartBeats{
				Ip:		heartbeat["ip"],
				Name:		heartbeat["name"],
				Country:	heartbeat["country"],
				Area:		heartbeat["area"],
				Province:	heartbeat["province"],
				City:		heartbeat["city"],
				Isp:		heartbeat["isp"],
				MonitorId:	heartbeat["monitorId"],
				NipingT:	nipingT,
				RunningTaskIds:	getTaskIds(),
			}
			jsons, _ := json.Marshal(heartbeats)

			req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsons))
			if err != nil {
				fmt.Println("Error:", err)
			}
			req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
			req.Header.Set("Content-Type","application/json")
			fmt.Println(req)
			client := &http.Client{}
			resp,err1 :=client.Do(req)
			fmt.Println(resp)
			if err1 != nil {
				log.Println("cannot get the response")
			}else {
				queue <- *resp
			}
			lastHeartBeat = time.Now().Unix()
		}
	}
}

func Consumer(queue <-chan http.Response){
	for true {
		resp := <- queue
		if resp.StatusCode == 200 {
			log.Println("received response")
			buf := new(bytes.Buffer)
			buf.ReadFrom(resp.Body)
			s := buf.String()
			json.Unmarshal([]byte(s), &monitorJob)
			fmt.Println(monitorJob.Data.TaskId)

			if monitorJob.Data.MonitorId == heartbeat["monitorId"] {
				switch monitorJob.Data.ActionType {
				case 0:
					log.Print("start task0")
					deleteMap(monitorJob.Data.TaskId, taskMap)
					log.Println("stop task:" +  monitorJob.Data.TaskId)
					break
				case 1:
					log.Print("start task1")
					startJob(monitorJob)
					break
				case 2:
					log.Print("start task2")
					deleteMap(monitorJob.Data.TaskId, taskMap)
					log.Println("stop task:" +  monitorJob.Data.TaskId)
					startJob(monitorJob)
					break
				}
			}
		}
	}
}

func Contains(l *list.List, value string) (bool, *list.Element) {
	for e := l.Front(); e != nil; e = e.Next() {
		if e.Value == value {
			return true, e
		}
	}
	return false, nil
}

func deleteNipingPid(l *list.List,e *list.Element) {
	if contain, element := Contains(l, e.Value.(string)); contain {
		l.Remove(element)
	}
}

// 取得niping进程pid，存在taskMap中用来杀进程
func findNipingPid(taskId string) (*list.List,*list.Element){
	cmd,err := exec.Command("cmd","/C",`tasklist`).Output()
	if err != nil {
		fmt.Println(err.Error())
	}
	array := strings.Split(string(cmd),"\n")
	l := list.New()
	for i :=0;i< len(array);i++ {
		nipingFlag,_ := regexp.Match("^niping.exe.*",[]byte(array[i]))
		if nipingFlag == true {
			pid := strings.Fields(array[i])[1]
			l.PushBack(pid)
			fmt.Println(l)
		}
	}
	pid := l.Back()
	taskMap[taskId] = l
	return l,pid
}

// 根据pid删除
func deleteMap(taskId string, taskMap map[string] *list.List){
	for e := taskMap[taskId].Front(); e != nil; e = e.Next() {
		fmt.Print(e.Value) //输出l2的值,460123
		_,err := exec.Command("cmd","/C","taskkill /pid",e.Value.(string),"/f").Output()
		if err != nil {
			fmt.Println(err.Error())
		}
	}
	delete(taskMap,taskId)
}

func NipingCMD(typeId int,taskId string, router string, nipingaddr string, b_args int, l_args int, d_args int,executeid int, channel chan MonitorResult) {
	startTime := time.Now().Unix()
	cmd := exec.Command("cmd", "/C", nipingaddr +"niping","-c","-H",router,"-B",strconv.Itoa(b_args),
		"-L", strconv.Itoa(l_args), "-D", strconv.Itoa(d_args))
	stdout, err := cmd.StdoutPipe()
	if err != nil {
		fmt.Println("StdoutPipe: " + err.Error())
	}
	stderr, err := cmd.StderrPipe()
	if err != nil {
		fmt.Println("StderrPipe: ", err.Error())
	}
	if err := cmd.Start(); err != nil {
		fmt.Println("Start: ", err.Error())
	}
	bytesErr, err := ioutil.ReadAll(stderr)
	if err != nil {
		fmt.Println("ReadAll stderr: ", err.Error())
	}
	if len(bytesErr) != 0 {
		endTime := time.Now().Unix()
		errArray := strings.Split(string(bytesErr),"\r")
		for i:=0;i< len(errArray);i++ {
			if len(strings.Fields(errArray[i])) == 3 {
				if strings.Fields(errArray[i])[1] == "ERRNO" {
					monitorResult.Errno = strings.Fields(errArray[i])[2]
					monitorResult.Errmsg = string(bytesErr)
				}
			}
		}
		monitorResult = MonitorResult{"","",endTime,monitorResult.Errmsg,monitorResult.Errno,
					      "","", startTime,taskId,"","",typeId}
		channel <- monitorResult
	}
	bytes, err := ioutil.ReadAll(stdout)
	if err != nil {
		fmt.Println("ReadAll stdout: ", err.Error())
	}
	if err := cmd.Wait(); err != nil {
		fmt.Println("Wait: ", err.Error())
	}
	endTime := time.Now().Unix()
	array := strings.Split(string(bytes[:]),"\r")
	switch executeid {
	case 0:
		for i := 0;i< len(array);i++ {
			if len(strings.Fields(array[i])) == 3 {
				switch strings.Fields(array[i])[0] {
				case "avg":
					monitorResult.Avg = strings.Fields(array[i])[1]
					break
				case "max":
					monitorResult.Max = strings.Fields(array[i])[1]
					break
				case "min":
					monitorResult.Min = strings.Fields(array[i])[1]
					break
				case "av2":
					monitorResult.Av2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		monitorResult = MonitorResult{monitorResult.Av2,monitorResult.Avg,endTime,"","",
					      monitorResult.Max,monitorResult.Min, startTime,taskId,"","",0}
		break
	case 1:
		for i := 0;i< len(array);i++ {
			if len(strings.Fields(array[i])) == 3 {
				switch strings.Fields(array[i])[0] {
				case "tr":
					monitorResult.Tr = strings.Fields(array[i])[1]
					break
				case "tr2":
					monitorResult.Tr2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		monitorResult  = MonitorResult{"","",0,"","",
					       "","", 0,taskId,monitorResult.Tr,monitorResult.Tr2,0}
		break
	default:
		for i := 0;i< len(array);i++ {
			if len(strings.Fields(array[i])) == 3 {
				switch strings.Fields(array[i])[0] {
				case "avg":
					monitorResult.Av2 = strings.Fields(array[i])[1]
					break
				case "max":
					monitorResult.Max = strings.Fields(array[i])[1]
					break
				case "min":
					monitorResult.Min = strings.Fields(array[i])[1]
					break
				case "av2":
					monitorResult.Av2 = strings.Fields(array[i])[1]
					break
				case "tr":
					monitorResult.Tr = strings.Fields(array[i])[1]
					break
				case "tr2":
					monitorResult.Tr2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		monitorResult  = MonitorResult{monitorResult.Av2,"",endTime,"","",
			monitorResult.Max,monitorResult.Min, startTime,taskId,monitorResult.Tr,monitorResult.Tr2,0}
		break
	}
	channel <- monitorResult
}

func main(){
	SendHeartBeat()
}
