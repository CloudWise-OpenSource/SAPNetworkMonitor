package sche

import (
	"github.com/jasonlvhit/gocron"
	"SAPNetworkMonitor/monitor/src/cli"
	"SAPNetworkMonitor/monitor/src/models"
	"strconv"
)

var(
	cronJob = make(map[string] *Cron)
)

type Cron struct {
	schedulers []*gocron.Scheduler
}

func StartJob(monitorJob models.MonitorJob,serverInfo map[string] string,monitorInfo map[string] string,taskMap map[string] string) {
	url := serverInfo["dataServerUrl"] + "/api/databus/monitor/" + monitorInfo["monitorId"] + "/result"
	interval, _ := strconv.ParseUint(serverInfo["nipingtInterval"], 0, 64)
	cronJob[monitorJob.Data.TaskId] = new(Cron)
	go func(){
		s := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,s)
		s.Every(interval).Seconds().Do(cli.DelayAndBrandwidth,url,monitorJob,serverInfo,taskMap)
		<- s.Start()
	}()

	go func() {
		t := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,t)
		t.Every(interval).Seconds().Do(cli.StabilityTask,url,monitorJob,serverInfo,taskMap)
		<- t.Start()
	}()

	go func() {
		u := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers,u)
		u.Every(interval).Seconds().Do(cli.TimeoutTask,url,monitorJob,serverInfo,taskMap)
		<- u.Start()
	}()

}
