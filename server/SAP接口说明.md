接口：
一、心跳

说明：Monitor以1分钟的频率调用接口发送心跳信息（Monitor对象），服务器端会针对当前监测点进行判断，如果有要执行的作业，会返回作业信息（MonitorJob对象）。

/api/monitors/monitor/{monitorId}/heartbeat

Method: POST
MediaType： application/json

Request:

{
    "area":"华北",
    "city":"三河",
    "country":"中国",
    "ip":"10.0.1.112",
    "isp":"中国电信",
    "monitorId":"mid1",
    "name":"XXX公司三河财务部",
    "province":"河北",
    "nipingT":"niping -t 的结果",
    "runningTaskIds":[
        "task6",
        "task2"
    ]
}

说明：

1. runningTaskIds 表示Monitor端正在运行的任务
2. nipingT 为niping -t 的结果。

Response:

{
    "code":1000,
    "data":{
        "actionType":2,
        "interval":2,
        "jobDesc":{
            "bandwidthB":100000,
            "bandwidthL":10,
            "idleTimeoutD":3600000,
            "roundTripTimeB":1,
            "roundTripTimeL":100,
            "router":"/H/10.0.1.1/H/192.168.1.1",
            "stabilityB":200,
            "stabilityD":1000,
            "stabilityL":36000
        },
        "modifiedTime":1496396029625,
        "monitorId":"mid1",
        "taskId":"taskId1"
    }
}

说明:

1. actionType 有三个值，	STOP = 0， START = 1，RESTART = 2 ，分别表示停止当前Job,启动当前Job,停止旧的Job并启动新的（主要对应任务更新）
2. interval 为Job执行的频率，单位是分钟。
3. jobDesc 为Job的描述信息，bandwidth为带宽，idleTimeout为空闲，roundTripTime为时延，stability为稳定性，其后缀B,L,D实际上为niping命令中的参数值 ，分别表示包大小，循环次数，时间间隔。
4. router 为路由

二、数据回传

/api/databus/monitor/{monitorId}/result

说明：发送监测结果给服务端（MonitorNipingResult对象）

Method: POST
MediaType： application/json

Request:

{
    "av2":100,
    "avg":80,
    "endTime":1496398245159,
    "errmsg":"niping error result.",
    "errno":13923,
    "max":100,
    "min":30,
    "startTime":1496398245159,
    "taskId":"taskId111",
    "tr":3923,
    "tr2":323,
    "type":0
}

说明：
1.type 表示返回的结果是时延和带宽、稳定性、闲置超时 3种情况。分别用0，1，2表示
2.startTime与endTime 为niping开发执行的时间 和结果的时间，为毫秒值。
3.errno 如果niping出错，比如网络不通，会有一个errno。errmsg 为完整的出错信息。
4.taskId为当前任务的ID
5.其他参数对应niping的结果。其中type为0时，需要执行两次niping,一次是时延，一次是带宽。tr和tr2应该取第2次的。


Response:

{
    "code":1000,
    "msg":"success"
}