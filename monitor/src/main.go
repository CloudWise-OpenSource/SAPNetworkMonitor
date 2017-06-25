package main

import (
	"github.com/kardianos/service"
	"log"
	"os"
	"SAPNetworkMonitor/monitor/src/cfg"
	"strconv"
	"github.com/jasonlvhit/gocron"
	"SAPNetworkMonitor/monitor/src/sche"
)
type program struct{}

func (p *program) Start(s service.Service) error {
	log.Println("开始服务")
	go p.run()
	return nil
}
func (p *program) Stop(s service.Service) error {
	log.Println("停止服务")
	return nil
}
func (p *program) run() {
	// 这里放置程序要执行的代码……
	cfg.LogConfig()
	monitorInfo,serverInfo := cfg.ReadConfig()
	url := serverInfo["heartbeatServerUrl"] + "/api/monitors/monitor/" + monitorInfo["monitorId"] + "/heartbeat"
	heartbeatInterval,_ := strconv.ParseUint(serverInfo["heartbeatInterval"],0,64)
	nipingtInterval,_ := strconv.ParseInt(serverInfo["nipingtInterval"],10,64)
	gocron.Every(heartbeatInterval).Seconds().Do(sche.HeartBeat,url,nipingtInterval,serverInfo,monitorInfo)
	<- gocron.Start()

}

func main() {
	//Call this function where the action happpens
	//服务的配置信息
	cfg := &service.Config{
		Name:        "SAP",
		DisplayName: "SAP",
		Description: "This is an example Go service.",
	}
	// Interface 接口
	prg := &program{}
	// 构建服务对象
	s, err := service.New(prg, cfg)
	if err != nil {
		log.Fatal(err)
	}
	// logger 用于记录系统日志
	logger, err := s.Logger(nil)
	if err != nil {
		log.Fatal(err)
	}
	if len(os.Args) == 2 { //如果有命令则执行
		err = service.Control(s, os.Args[1])
		if err != nil {
			log.Fatal(err)
		}
	} else { //否则说明是方法启动了
		err = s.Run()
		if err != nil {
			logger.Error(err)
		}
	}
	if err != nil {
		logger.Error(err)
	}
}




