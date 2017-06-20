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
	"strings"
	"regexp"
	"container/list"
	"strconv"
	"bytes"
	"io/ioutil"
	"github.com/jasonlvhit/gocron"
	oss "os"
	"runtime"
)

var (
	configFile = flag.String("configfile", "config.ini", "General configuration file")
	heartbeat = make(map[string] string)
	serverInfo = make(map[string] string)
	taskMap = make(map[string] *list.List)
	monitorResult MonitorResult
	cronJob = make(map[string] *Cron)
	lastNipingT int64
	stabilityFlag bool
	timeoutFlag bool
	command string
	method string
)

type Cron struct {
	schedulers []*gocron.Scheduler
}

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
			BandwithB	int		`json:"bandwidthB"`
			BandwithL	int		`json:"bandwidthL"`
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
	stabilityFlag = true
	timeoutFlag = true
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

func GetNipingT(nipingPath string,nipingtInterval int64) (string,bool) {
	nipingT := ""
	if time.Now().Unix() - lastNipingT > nipingtInterval {
		cmd,err := exec.Command(command,method,nipingPath,"-t").Output()
		if err != nil {
			nipingT := "Configuration Present Error: " + "nipingPath: " + serverInfo["nipingPath"] +
				" heartbeatServerUrl: " + serverInfo["heartbeatServerUrl"] + " dataServerUrl: " + serverInfo["dataServerUrl"]
			log.Println(nipingT)
			return nipingT,true
		}
		nipingT = string(cmd)
		lastNipingT = time.Now().Unix()
	}
	return nipingT,false
}

func readConfig() {
	cfg, err := config.ReadDefault(*configFile)
	if err != nil {
		log.Fatalf("Fail to find", *configFile, err)
	}
	if cfg.HasSection("monitorInfo") {
		section, err := cfg.SectionOptions("monitorInfo")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("monitorInfo", v)
				if err == nil {
					heartbeat[v] = options
				}
			}
		}
	}
	if cfg.HasSection("serverInfo") {
		section, err := cfg.SectionOptions("serverInfo")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("serverInfo", v)
				if err == nil {
					serverInfo[v] = options
				}
			}
		}
	}
}

// 时延和带宽
func DelayAndBrandwidth(url string,monitorJob MonitorJob) {
	log.Println("Start DelayAndBrandwidth")
	fmt.Println("@@@@@@@@@" + strconv.Itoa(monitorJob.Data.Interval))  // job没有变
	fmt.Println("{{{{{{{{{{{" + strconv.Itoa(len(taskMap)))
	if _,ok := taskMap[monitorJob.Data.TaskId];ok {
		channel_result1 := make(chan MonitorResult,1)
		channel_result2 := make(chan MonitorResult,1)
		go	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,0,0,channel_result1)
		go	NipingCMD(0,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
			monitorJob.Data.JobDesc.RoundTripTimeB, monitorJob.Data.JobDesc.RoundTripTimeL,0,1,channel_result2)
		list_a,pid_a := findNipingPid(monitorJob.Data.TaskId)
		list_b,pid_b := findNipingPid(monitorJob.Data.TaskId)
		if list_a.Len() != 0 {
			deleteNipingPid(list_a,pid_a)
		}
		if list_b.Len() != 0 {
			deleteNipingPid(list_b,pid_b)
		}
		monitorResult_a := <- channel_result1
		monitorResult_b := <- channel_result2

		monitorResult = MonitorResult{monitorResult_a.Av2,monitorResult_a.Avg,monitorResult_a.EndTime,monitorResult_a.Errmsg,
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
	}else {

	}
}

//稳定性
func StabilityTask(url string,monitorJob MonitorJob){
	if stabilityFlag == true{
		log.Println("Start StabilityTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			stabilityFlag = false
			channel_result := make(chan MonitorResult,1)
			go NipingCMD(1,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
				monitorJob.Data.JobDesc.StabilityB,monitorJob.Data.JobDesc.StabilityL,monitorJob.Data.JobDesc.StabilityD,2,channel_result)
			list,pid := findNipingPid(monitorJob.Data.TaskId)
			monitorResult_stab := <- channel_result
			if list.Len() != 0 {
				deleteNipingPid(list,pid)
			}
			monitorResult = MonitorResult{monitorResult_stab.Av2,monitorResult_stab.Avg,monitorResult_stab.EndTime,monitorResult_stab.Errmsg,
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
func TimeoutTask(url string,monitorJob MonitorJob){
	if timeoutFlag == true{
		log.Println("tart TimeoutTask")
		if _,ok := taskMap[monitorJob.Data.TaskId];ok {
			timeoutFlag = false
			channel_result := make(chan MonitorResult,1)
			go NipingCMD(2,monitorJob.Data.TaskId,monitorJob.Data.JobDesc.Router,serverInfo["nipingPath"],
				monitorJob.Data.JobDesc.BandwithB,monitorJob.Data.JobDesc.BandwithL,monitorJob.Data.JobDesc.IdleTimeoutD,2,channel_result)
			list,pid := findNipingPid(monitorJob.Data.TaskId)
			monitorResult_idle := <- channel_result
			if list.Len() != 0 {
				deleteNipingPid(list,pid)
			}
			monitorResult = MonitorResult{monitorResult_idle.Av2,monitorResult_idle.Avg,monitorResult_idle.EndTime,monitorResult_idle.Errmsg,
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

func startJob(monitorJob MonitorJob) {
	url := serverInfo["dataServerUrl"] + "/api/databus/monitor/" + heartbeat["monitorId"] + "/result"
	fmt.Println("%%%%%%%%%" + monitorJob.Data.TaskId)
	fmt.Println("111111111111==============================================================")
	cronJob[monitorJob.Data.TaskId] = new(Cron)
	go func(){
		s := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,s)
		s.Every(uint64(5)).Seconds().Do(DelayAndBrandwidth,url,monitorJob)
		<- s.Start()
	}()

	go func() {
		t := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,t)
		t.Every(uint64(5)).Seconds().Do(StabilityTask,url,monitorJob)
		<- t.Start()
	}()

	go func() {
		u := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,u)
		u.Every(uint64(5)).Seconds().Do(TimeoutTask,url,monitorJob)
		<- u.Start()
	}()

}

func SendHeartBeat() {
	readConfig()
	if runtime.GOOS == "windows" {
		command = "cmd"
		method = "/C"
	}else {
		command = "/bin/sh"
		method = "-c"
	}
	url := serverInfo["heartbeatServerUrl"] + "/api/monitors/monitor/" + heartbeat["monitorId"] + "/heartbeat"
	heartbeatInterval,_ := strconv.ParseUint(serverInfo["heartbeatInterval"],0,64)
	nipingtInterval,_ := strconv.ParseInt(serverInfo["nipingtInterval"],10,64)
	gocron.Every(heartbeatInterval).Seconds().Do(HeartBeat,url,nipingtInterval)
	<- gocron.Start()
}

func HeartBeat(url string,nipingtInterval int64){
	log.Println("start heartbeat")
	nipingT,errFlag := GetNipingT(serverInfo["nipingPath"],nipingtInterval)
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
	monitorJob := new(MonitorJob)
	req, err := http.NewRequest("POST", url, bytes.NewBuffer(jsons))
	if err != nil {
		fmt.Println("Error:", err)
	}
	req.Header.Set("Authorization","Bearer Zb3cVv0qzeNhYZwYbdC")
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
	if monitorJob.Data.MonitorId == heartbeat["monitorId"] {
		switch monitorJob.Data.ActionType {
		case 0:
			log.Print("start task0")
			deleteMap(monitorJob.Data.TaskId, taskMap)
			log.Println("stop task:" +  monitorJob.Data.TaskId)
			break
		case 1:
			log.Print("start task1")
			taskMap[monitorJob.Data.TaskId] = list.New()
			startJob(*monitorJob)
			break
		case 2:
			log.Print("start task2")
			deleteMap(monitorJob.Data.TaskId, taskMap)
			log.Println("stop task:" +  monitorJob.Data.TaskId)
			startJob(*monitorJob)
			break
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
	if runtime.GOOS == "windows" {
		cmd,err := exec.Command("cmd","/C",`tasklist`).Output()
		if err != nil {
			fmt.Println(err.Error())
		}
		array := strings.Split(string(cmd),"\n")
		l := list.New()
		for i :=0;i< len(array);i++ {
			niping := strings.Split(serverInfo["nipingPath"],"/")[len(strings.Split(serverInfo["nipingPath"],"/")) - 1]
			nipingFlag,_ := regexp.Match("^" + niping + ".*",[]byte(array[i]))
			if nipingFlag == true {
				pid := strings.Fields(array[i])[1]
				l.PushBack(pid)
			}
		}
		pid := l.Back()
		taskMap[taskId] = l
		return l,pid
	}else {
		cmd,err := exec.Command("/bin/sh", "-c", `ps -aux | grep niping | grep -v grep`).Output()
		if err != nil {
			fmt.Println(err.Error())
		}
		array := strings.Split(string(cmd),"\n")
		l := list.New()
		for i :=0;i< len(array);i++ {
			pid := strings.Fields(array[i])[1]
			l.PushBack(pid)
		}
		pid := l.Back()
		taskMap[taskId] = l
		return l,pid
	}
}

// 根据pid删除
func deleteMap(taskId string, taskMap map[string] *list.List){
	if _,ok := taskMap[taskId];ok {
		if runtime.GOOS == "windows" {
			for e := taskMap[taskId].Front(); e != nil; e = e.Next() {
				fmt.Print(e.Value)
				_,err := exec.Command("cmd","/C","taskkill /pid",e.Value.(string),"/f").Output()
				if err != nil {
					fmt.Println(err.Error())
				}
			}
		}else {
			for e := taskMap[taskId].Front(); e != nil; e = e.Next() {
				fmt.Print(e.Value)
				_,err := exec.Command("/bin/sh","-c","kill -9",e.Value.(string)).Output()
				if err != nil {
					fmt.Println(err.Error())
				}
			}
		}
		delete(taskMap,taskId)
		fmt.Println("(((((((((((((((((((((")
		fmt.Println(len(cronJob[taskId].schedulers))
		for _,v := range cronJob[taskId].schedulers {
			v.Clear()
		}
	}
}

func NipingCMD(typeId int,taskId string, router string, nipingPath string, b_args int, l_args int,
	d_args int,executeid int, channel chan MonitorResult) {
	startTime := time.Now().Unix()
		cmd := exec.Command(command, method, nipingPath,"-c","-H",router,"-B",strconv.Itoa(b_args),
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
		//return
	}
	bytes, err := ioutil.ReadAll(stdout)
	if err != nil {
		fmt.Println("ReadAll stdout: ", err.Error())
	}
	endTime := time.Now().Unix()
	array := strings.Split(string(bytes[:]),"\r")
	switch executeid {
	case 0:
		log.Println("case 0")
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
		log.Println("case 1")
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
		log.Println("case others")
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
				case "tr":
					monitorResult.Tr = strings.Fields(array[i])[1]
					break
				case "tr2":
					monitorResult.Tr2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		monitorResult  = MonitorResult{monitorResult.Av2,monitorResult.Avg,endTime,"","",
									   monitorResult.Max,monitorResult.Min, startTime,taskId,monitorResult.Tr,monitorResult.Tr2,typeId}
		break
	}

	fmt.Println(monitorResult)
	channel <- monitorResult
}

func main(){
	SendHeartBeat()
}
