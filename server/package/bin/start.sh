#!/bin/bash

PWDPATH=`dirname $0`
PORTAL_HOME=`cd $PWDPATH && cd .. && pwd`
echo $PORTAL_HOME
cd $PORTAL_HOME
JVM_OPTS="
-server 
 -Xms1g
 -Xmx1g
 -XX:NewSize=512m
 -XX:SurvivorRatio=6
 -XX:+AlwaysPreTouch
 -XX:+UseG1GC
 -XX:MaxGCPauseMillis=2000
 -XX:GCTimeRatio=4
 -XX:InitiatingHeapOccupancyPercent=30
 -XX:G1HeapRegionSize=8M
 -XX:ConcGCThreads=2
 -XX:G1HeapWastePercent=10
 -XX:+UseTLAB
 -XX:+ScavengeBeforeFullGC
 -XX:+DisableExplicitGC
 -XX:+PrintGCDetails
 -XX:-UseGCOverheadLimit
 -XX:+PrintGCDateStamps
 -Xloggc:logs/gc.log
"

action=$1
if [ x$action == x ] ;then  
    start() {
	nohup java $JVM_OPTS -jar lib/sap-network-monitor-server-1.0.2.jar server conf/server.yml &
	echo -e '\r'
    }	
    start|tee logs/console.log |tee logs/console.log
elif [ $action == "migrate" ] ;then
    migrate() {
	nohup java $JVM_OPTS -jar lib/sap-network-monitor-server-1.0.2.jar db migrate conf/server.yml --migrations conf/migrations.xml  &
	echo -e '\r'
    }	
    migrate|tee logs/console.log |tee logs/console.log
else 
    unknown_arg() {
	echo -e "unknown argument\r"
    }
    unknown_arg|tee logs/console.log |tee logs/console.log
fi