package main

import (
	"strconv"
	"github.com/jasonlvhit/gocron"
	"runtime"
	"SAPNetworkMonitor/monitor/src/cfg"
	"SAPNetworkMonitor/monitor/src/sche"
)

var (
	monitorInfo = make(map[string] string)
	serverInfo = make(map[string] string)
	osm = make(map[string] string)
)

func OperatingSystem() (mapos map[string] string) {
	if runtime.GOOS == "windows" {
		osm["command"] = "cmd"
		osm["method"] = "/C"
	}else {
		osm["command"] = "/bin/sh"
		osm["method"] = "-c"
	}
	return osm
}

func main(){
	monitorInfo,serverInfo = cfg.ReadConfig()
	OperatingSystem()
	url := serverInfo["heartbeatServerUrl"] + "/api/monitors/monitor/" + monitorInfo["monitorId"] + "/heartbeat"
	heartbeatInterval,_ := strconv.ParseUint(serverInfo["heartbeatInterval"],0,64)
	nipingtInterval,_ := strconv.ParseInt(serverInfo["nipingtInterval"],10,64)
	gocron.Every(heartbeatInterval).Seconds().Do(sche.HeartBeat,url,nipingtInterval,serverInfo,monitorInfo,osm)
	<- gocron.Start()
}
