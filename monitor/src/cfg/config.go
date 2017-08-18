package cfg

import (
	"log"
	"github.com/larspensjo/config"
	"os"
	"path/filepath"
	"strings"
	"github.com/saintfish/chardet"
	"github.com/axgle/mahonia"
	"fmt"
)

var (
	monitorInfo = make(map[string] string)
	serverInfo = make(map[string] string)

)

type MonitorInfo struct {
	Ip			string
	Name		string
	Country		string
	Area		string
	Province	string
	City		string
	Isp			string
	MoniterId	string
	AccessToken	string
}

type ServerInfo struct {
	NipingPath			string
	HeartbeatInterval	string
	NipingtInterval		string
	HeartbeatServerUrl	string
	DataServerUrl		string
}

func GetCurrentDirectory() string {
	dir, err := filepath.Abs(filepath.Dir(os.Args[0]))
	if err != nil {
		log.Fatal(err)
	}
	return strings.Replace(dir, "\\", "/", -1)
}

func ExampleTextDetector(option string) string {
	data := []byte(option)
	detector := chardet.NewTextDetector()
	result, err := detector.DetectBest(data)
	if err == nil {
		return result.Charset
	}
	return result.Charset

	// Output:
	// Detected charset is GB-18030, language is zh
}

func ReadConfig()(map1 map[string] string,map2 map[string] string) {
	path := GetCurrentDirectory()
	configFile := path + "/config.ini"
	cfg, err := config.ReadDefault(configFile)
	data := ""
	if err != nil {
		log.Fatalf("Fail to find", configFile, err)
	}
	if cfg.HasSection("monitorInfo") {
		section, err := cfg.SectionOptions("monitorInfo")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("monitorInfo", v)
				if err == nil {
					decoder := mahonia.NewDecoder("gb18030")
					data = decoder.ConvertString(options)
					fmt.Println(data)
					monitorInfo[v] = data
				}
			}
		}
	}
	if cfg.HasSection("serverInfo") {
		section, err := cfg.SectionOptions("serverInfo")
		if err == nil {
			for _, v := range section {
				options, err := cfg.String("serverInfo", v)
				if err == nil {
					serverInfo[v] = options
				}
			}
		}
	}
	return monitorInfo,serverInfo
}
