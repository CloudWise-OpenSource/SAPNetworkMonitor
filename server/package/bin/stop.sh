#!/bin/bash
ps -ef|grep sap-network-monitor-server-1.0.0.jar|grep -v grep|awk '{print $2}'|xargs kill -9
