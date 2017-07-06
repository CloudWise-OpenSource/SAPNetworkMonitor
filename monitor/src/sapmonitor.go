package main

import (
	"github.com/kardianos/service"
	"log"
	"os"
	"SAPNetworkMonitor/monitor/src/cfg"
	"strconv"
	"github.com/jasonlvhit/gocron"
	"SAPNetworkMonitor/monitor/src/sche"
	"runtime"
)
type program struct{}

func (p *program) Start(s service.Service) error {
	log.Println("Start Service")
	go p.run()
	return nil
}
func (p *program) Stop(s service.Service) error {
	log.Println("Stop Service")
	return nil
}
func (p *program) run() {
	cfg.LogConfig()
	monitorInfo,serverInfo := cfg.ReadConfig()
	heartbeatInterval,_ := strconv.ParseUint(serverInfo["heartbeatInterval"],0,64)
	nipingtInterval,_ := strconv.ParseInt(serverInfo["nipingtInterval"],10,64)
	runtime.GOMAXPROCS(runtime.NumCPU())
	gocron.Every(heartbeatInterval).Seconds().Do(sche.HeartBeat,nipingtInterval,serverInfo,monitorInfo)
	<- gocron.Start()

}

func main() {
	cfg := &service.Config{
		Name:        "SAP-Monitor",
		DisplayName: "SAP-Monitor",
		Description: "This is an SAP-Monitor.",
	}
	prg := &program{}
	s, err := service.New(prg, cfg)
	if err != nil {
		log.Fatal(err)
	}
	logger, err := s.Logger(nil)
	if err != nil {
		log.Fatal(err)
	}
	if len(os.Args) == 2 {
		err = service.Control(s, os.Args[1])
		if err != nil {
			log.Fatal(err)
		}
	} else {
		err = s.Run()
		if err != nil {
			logger.Error(err)
		}
	}
	if err != nil {
		logger.Error(err)
	}
}




