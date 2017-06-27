package cli
import (
	"github.com/snluu/uuid"
	"os"
	"io/ioutil"
	"flag"
	"SAPNetworkMonitor/monitor/src/cfg"
	"log"
)

func PathExists(path string) (bool, error) {
	_, err := os.Stat(path)
	if err == nil {
		return true, nil
	}
	if os.IsNotExist(err) {
		return false, nil
	}
	return false, err
}

func GetMonitorId() string {
	uuid := uuid.Rand()
	path,err := cfg.Home()
	if err != nil {
		log.Println("Cannot Get Homepath")
		return ""
	}
	fileName := flag.String("monitorid", path + "/.sapmonitorid", "mointorid file name")
	existFlag,_ := PathExists(*fileName)
	if existFlag {
		f, _ := os.Open(*fileName)
		monitorId,_ := ioutil.ReadAll(f)
		return string(monitorId)
	}else {
		dstFile,_ := os.Create(*fileName)
		defer dstFile.Close()
		s:= uuid.Hex()
		dstFile.WriteString(s)
		return s
	}
}

