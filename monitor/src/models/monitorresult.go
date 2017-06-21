package models

type MonitorResult struct {
	Av2      		string        	`json:"av2"`
	Avg			string		`json:"avg"`
	EndTime       		int64        	`json:"endTime"`
	Errmsg      		string 	    	`json:"errmsg"`
	Errno          		string	    	`json:"errno"`
	Max            		string	    	`json:"max"`
	Min      		string        	`json:"min"`
	StartTime		int64		`json:"startTime"`
	TaskId			string		`json:"taskId"`
	Tr  			string	    	`json:"tr"`
	Tr2			string	    	`json:"tr2"`
	Type			int		`json:"type"`
}
