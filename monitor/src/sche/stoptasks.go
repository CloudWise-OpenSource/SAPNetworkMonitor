package sche

import (
	"SAPNetworkMonitor/monitor/src/cli"
)

func StopTask(taskId string, taskMap map[string] string){
	if _,ok := taskMap[taskId];ok {
		delete(taskMap,taskId)
		for _,v := range cronJob[taskId].schedulers {
			v.Remove(cli.DelayAndBrandwidth)
			v.Remove(cli.StabilityTask)
			v.Remove(cli.TimeoutTask)
			v.Clear()
		}
	}
}
