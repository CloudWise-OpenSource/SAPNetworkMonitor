package cli
import (
	"github.com/anarcher/shortuuid"
	"os"
	"io/ioutil"
	"SAPNetworkMonitor/monitor/src/cfg"
	"log"
)

var (
	monitorId string
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
	if monitorId == "" {
		uuid := shortuuid.New()
		path,err := cfg.Home()
		if err != nil {
			log.Println("Cannot Get Homepath")
			return ""
		}
		fileName := path + "/.sapmonitorid"
		existFlag,_ := PathExists(fileName)
		if existFlag {
			f, _ := os.Open(fileName)
			readMonitorId,_ := ioutil.ReadAll(f)
			monitorId = string(readMonitorId)
			return monitorId
		}else {
			dstFile,_ := os.Create(fileName)
			defer dstFile.Close()
			s:= uuid.String()
			dstFile.WriteString(s)
			return s
		}
	}else{
		return monitorId
	}

}

