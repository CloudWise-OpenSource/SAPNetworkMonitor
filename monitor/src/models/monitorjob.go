package models

type MonitorJob struct {
	Code          	int64 		`json:"code"`
	Message		string		`json:"msg"`
	Data       	struct {
		MonitorId	string		`json:"monitorId"`
		TaskId		string		`json:"taskId"`
		Interval	int		`json:"interval"`
		ActionType	int		`json:"actionType"`
		JobDesc		struct {
			Router		string		`json:"router"`
			RoundTripTimeB	int		`json:"roundTripTimeB"`
			RoundTripTimeL	int		`json:"roundTripTimeL"`
			BandwithB	int		`json:"bandwidthB"`
			BandwithL	int		`json:"bandwidthL"`
			StabilityB	int		`json:"stabilityB"`
			StabilityD	int		`json:"stabilityD"`
			StabilityL	int		`json:"stabilityL"`
			IdleTimeoutD	int		`json:"idleTimeoutD"`
		}	`json:"jobDesc"`
		ModifiedTime	int64		`json:"modifiedTime"`
	}	`json:"data"`
}