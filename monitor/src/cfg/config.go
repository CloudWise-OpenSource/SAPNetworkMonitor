package cfg

import (
	"log"
	"github.com/larspensjo/config"
	"flag"
)

var (
	configFile = flag.String("configfile", "monitor/config.ini", "General configuration file")
	monitorInfo = make(map[string] string)
	serverInfo = make(map[string] string)
)

type MonitorInfo struct {
	Ip			string
	Name		string
	Country		string
	Area		string
	Province	string
	City		string
	Isp			string
	MoniterId	string
	AccessToken	string
}

type ServerInfo struct {
	NipingPath			string
	HeartbeatInterval	string
	NipingtInterval		string
	HeartbeatServerUrl	string
	DataServerUrl		string
}

func ReadConfig() (map1 map[string] string,map2 map[string] string) {
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
					monitorInfo[v] = options
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
	return monitorInfo,serverInfo
}