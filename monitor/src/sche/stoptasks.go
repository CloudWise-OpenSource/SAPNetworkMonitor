package sche

import (
	"container/list"
	"runtime"
	"fmt"
	"os/exec"
)

func StopTask(taskId string, taskMap map[string] *list.List){
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
