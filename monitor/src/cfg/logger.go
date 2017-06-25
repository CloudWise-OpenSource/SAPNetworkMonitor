package cfg

import (
	"flag"
	"os"
	"fmt"
	"log"
)

func LogConfig() {
	//set logfile Stdout
	path := getCurrentDirectory()
	logFileName := flag.String("log", path + "/monitor.log", "Log file name")
	logFile, logErr := os.OpenFile(*logFileName, os.O_CREATE|os.O_RDWR|os.O_APPEND, 0666)
	if logErr != nil {
		fmt.Println("Fail to find", *logFile, "Monitor start Failed")
		os.Exit(1)
	}
	log.SetOutput(logFile)
	log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)

}
