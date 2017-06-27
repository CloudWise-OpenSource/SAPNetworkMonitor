# SAPNetworkMonitor
Based on niping for sap network monitoring

This monitor is used to monitor network performance in each SAP-Agent by sending heartbeat to get monitoring task from SAP-Server and it supports Windows and Linux.
For convenience, users can set this monitor as Windows and Linux service.
There are compiled Apps in the project.Users should modify the config.ini according to the actual case and the config.ini should be put in the same directory as the App.

1.How to Run it
main.exe

2.How to Set it as Service
Set the App as operating system service
main.exe install
Start the service 
main.exe start
Stop the service
main.exe stop
Uninstall the service 
main.exe uninstall


