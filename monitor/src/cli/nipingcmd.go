package cli

import (
	"time"
	"os/exec"
	"log"
	"strconv"
	"fmt"
	"io/ioutil"
	"strings"
	"SAPNetworkMonitor/monitor/src/models"
	"SAPNetworkMonitor/monitor/src/cfg"
	"runtime"
)

var (
	lastNipingT int64
)

func GetNipingT(nipingPath string,nipingtInterval int64) (string,bool) {
	nipingT := ""
	_,serverInfo := cfg.ReadConfig()
	if time.Now().Unix() - lastNipingT > nipingtInterval {
		cmd,err := exec.Command(nipingPath,"-t").Output()
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

func NipingCMD(typeId int,taskId string, router string, nipingPath string, b_args int, l_args int,
	d_args int,executeid int, channel chan models.MonitorResult) {
	monitorResult := new(models.MonitorResult)
	startTime := time.Now().Unix()
	cmd := exec.Command(nipingPath,"-c","-H",router,"-B",strconv.Itoa(b_args),"-L",strconv.Itoa(l_args),"-D",strconv.Itoa(d_args))
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
		if runtime.GOOS =="windows" {
			errArray := strings.Split(string(bytesErr),"\r")
			for i:=0;i< len(errArray);i++ {
				if len(strings.Fields(errArray[i])) == 3 {
					if strings.Fields(errArray[i])[1] == "ERRNO" {
						monitorResult.Errno = strings.Fields(errArray[i])[2]
						monitorResult.Errmsg = string(bytesErr)
					}
				}
			}
		}else {
			errArray := []string{}
			if runtime.GOOS == "windows" {
				errArray = strings.Split(string(bytesErr),"\r")
			}else {
				errArray = strings.Split(string(bytesErr),"\n")
			}
			for i:=0;i< len(errArray);i++ {
				if len(strings.Fields(errArray[i])) == 3 {
					if strings.Fields(errArray[i])[1] == "ERRNO" {
						monitorResult.Errno = strings.Fields(errArray[i])[2]
						monitorResult.Errmsg = string(bytesErr)
					}
				}
			}
		}
		monitorResult.StartTime = startTime
		monitorResult.EndTime = endTime
		monitorResult.TaskId = taskId
		monitorResult.Type = typeId
		channel <- *monitorResult
	}
	bytes, err := ioutil.ReadAll(stdout)
	if err != nil {
		fmt.Println("ReadAll stdout: ", err.Error())
	}
	endTime := time.Now().Unix()
	array := []string{}
	if runtime.GOOS == "windows" {
		array = strings.Split(string(bytes[:]),"\r")
	}else {
		array = strings.Split(string(bytes[:]),"\n")
	}
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
		break
	}
	monitorResult.StartTime = startTime
	monitorResult.EndTime = endTime
	monitorResult.TaskId = taskId
	monitorResult.Type = typeId
	channel <- *monitorResult
}
