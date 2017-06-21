package cli

import (
	"container/list"
	"strings"
	"regexp"
	"os/exec"
	"fmt"
	"runtime"
)

func DeleteNipingPid(l *list.List,e *list.Element) {
	if contain, element := Contains(l, e.Value.(string)); contain {
		l.Remove(element)
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

// 取得niping进程pid，存在taskMap中用来杀进程
func FindNipingPid(taskId string,serverInfo map[string] string,taskMap map[string] *list.List) (*list.List,*list.Element){
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
