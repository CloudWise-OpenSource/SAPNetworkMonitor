package sche

import (
	"SAPNetworkMonitor/monitor/src/cli"
	"SAPNetworkMonitor/monitor/src/models"
	"github.com/jasonlvhit/gocron"
)

var (
	cronJob = make(map[string]*Cron)
)

type Cron struct {
	schedulers []*gocron.Scheduler
}

func StartJob(monitorJob models.MonitorJob, serverInfo map[string]string, monitorInfo map[string]string,taskMap map[string]string,errno int) {
	url := serverInfo["dataServerUrl"] + "/api/databus/monitor/" + monitorJob.Data.MonitorId + "/result"
	intervalSeconds := monitorJob.Data.Interval * 60
	interval := uint64(intervalSeconds)
	cronJob[monitorJob.Data.TaskId] = new(Cron)
	go func() {
		s := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers, s)
		s.Every(interval).Seconds().Do(cli.DelayAndBrandwidth, url, monitorJob, serverInfo, monitorInfo,taskMap,errno)
		<-s.Start()
	}()

	go func() {
		t := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers, t)
		t.Every(interval).Seconds().Do(cli.StabilityTask, url, monitorJob, serverInfo, monitorInfo,taskMap,errno)
		<-t.Start()
	}()

	go func() {
		u := gocron.NewScheduler()
		cronJob[monitorJob.Data.TaskId].schedulers = append(cronJob[monitorJob.Data.TaskId].schedulers, u)
		u.Every(interval).Seconds().Do(cli.TimeoutTask, url, monitorJob, serverInfo, monitorInfo,taskMap,errno)
		<-u.Start()
	}()

}
