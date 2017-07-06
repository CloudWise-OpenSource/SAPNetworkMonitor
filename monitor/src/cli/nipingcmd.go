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
	"runtime"
	"bytes"
)

var (
	lastNipingT int64
)

func GetNipingT(serverInfo map[string] string,nipingtInterval int64) (string,int) {
	nipingT := ""
	errno := 0
	if time.Now().Unix() - lastNipingT > nipingtInterval {
		cmd,err := exec.Command(serverInfo["nipingPath"],"-t").Output()
		if err != nil {
			cmderr := exec.Command(serverInfo["nipingPath"],"-t")
			w := bytes.NewBuffer(nil)
			cmderr.Stderr = w
			if err := cmderr.Run(); err != nil {
				log.Println(err.Error())
				nipingT = err.Error()
				if strings.Split(string(nipingT)," ")[2] == "permission" {
					errno = -2
				}
			}
			if w.Bytes() != nil {
				nipingT = string(w.Bytes())
			}

			lastNipingT = time.Now().Unix()
		}else {
			nipingT = string(cmd)
			lastNipingT = time.Now().Unix()
		}
	}
	return nipingT,errno
}

func transform(time string) string{
	inttime,_ := strconv.ParseFloat(time, 64)
	stringtime := strconv.FormatFloat(float64(inttime / 1000),'f',6,64)
	return stringtime
}

func NipingCMD(typeId int,taskId string, router string, nipingPath string, b_args int, l_args int,
	d_args int,executeid int,errno int) models.MonitorResult {
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
					if strings.Fields(errArray[i])[1] == "ERROR" {
						monitorResult.Errno = strings.Fields(errArray[i])[2]
						monitorResult.Errmsg = string(bytesErr)
					}else {
						monitorResult.Errno = "-1"
						monitorResult.Errmsg = string(bytesErr)
					}
				}
			}
		}else {
			errArray := strings.Split(string(bytesErr),"\n")
			for i:=0;i< len(errArray);i++ {
				if len(strings.Fields(errArray[i])) == 3 {
					if strings.Fields(errArray[i])[1] == "ERROR" {
						monitorResult.Errno = strings.Fields(errArray[i])[2]
						monitorResult.Errmsg = string(bytesErr)
					}else {
						monitorResult.Errno = "-1"
						monitorResult.Errmsg = string(bytesErr)
					}
				}
			}
		}
		monitorResult.StartTime = startTime
		monitorResult.EndTime = endTime
		monitorResult.TaskId = taskId
		monitorResult.Type = typeId
		return *monitorResult
	}
	bytes, err := ioutil.ReadAll(stdout)
	if err != nil {
		fmt.Println("ReadAll stdout: ", err.Error())
	}
	endTime := time.Now().Unix()
	array := []string{}
	unit := ""
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
					unit = strings.Fields(array[i])[2]
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


		if unit == "usecs" {
			monitorResult.Avg = transform(monitorResult.Avg)
			monitorResult.Max = transform(monitorResult.Max)
			monitorResult.Min = transform(monitorResult.Min)
			monitorResult.Av2 = transform(monitorResult.Av2)
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
				case "bw":
					monitorResult.Tr = strings.Fields(array[i])[1]
					break
				case "bw2":
					monitorResult.Tr2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		break
	case 2:
		log.Println("case 2")
		for i := 0;i< len(array);i++ {
			if len(strings.Fields(array[i])) == 3 {
				switch strings.Fields(array[i])[0] {
				case "avg":
					monitorResult.Avg = strings.Fields(array[i])[1]
					unit = strings.Fields(array[i])[2]
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
				case "bw":
					monitorResult.Tr = strings.Fields(array[i])[1]
					break
				case "bw2":
					monitorResult.Tr2 = strings.Fields(array[i])[1]
					break
				}
			}
		}
		if unit == "usecs" {
			monitorResult.Avg = transform(monitorResult.Avg)
			monitorResult.Max = transform(monitorResult.Max)
			monitorResult.Min = transform(monitorResult.Min)
			monitorResult.Av2 = transform(monitorResult.Av2)
		}
		break
	}
	if errno == -2 {
		monitorResult.Errno = "-2"
		monitorResult.Errmsg = "permission denied"
	}
	monitorResult.StartTime = startTime
	monitorResult.EndTime = endTime
	monitorResult.TaskId = taskId
	monitorResult.Type = typeId

	return *monitorResult
}
