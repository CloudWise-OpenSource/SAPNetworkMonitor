package main

import (
	"strconv"
	"github.com/jasonlvhit/gocron"
	"SAPNetworkMonitor/monitor/src/cfg"
	"SAPNetworkMonitor/monitor/src/sche"
)

var (
	monitorInfo = make(map[string] string)
	serverInfo = make(map[string] string)
)

func main(){
	monitorInfo,serverInfo = cfg.ReadConfig()
	url := serverInfo["heartbeatServerUrl"] + "/api/monitors/monitor/" + monitorInfo["monitorId"] + "/heartbeat"
	heartbeatInterval,_ := strconv.ParseUint(serverInfo["heartbeatInterval"],0,64)
	nipingtInterval,_ := strconv.ParseInt(serverInfo["nipingtInterval"],10,64)
	gocron.Every(heartbeatInterval).Seconds().Do(sche.HeartBeat,url,nipingtInterval,serverInfo,monitorInfo)
	<- gocron.Start()
}
