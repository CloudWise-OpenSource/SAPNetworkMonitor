package models

type HeartBeats struct {
	Ip		string		`json:"ip"`
	Name		string		`json:"name"`
	Country		string		`json:"country"`
	Area		string		`json:"area"`
	Province	string		`json:"province"`
	City		string		`json:"city"`
	Isp		string		`json:"isp"`
	MonitorId	string		`json:"monitorId"`
	NipingT		string		`json:"nipingT"`
	RunningTaskIds	[]string	`json:"runningTaskIds"`
	Version		string		`json:"version"`
}
