# SAPNetworkMonitor server

## compile 

```shell
git clone git@github.com:CloudWise-OpenSource/SAPNetworkMonitor.git

cd server

mvn clean install -Dmaven.test.skip=true 
```

```shell
.....
[INFO] --- maven-assembly-plugin:2.5.5:single (make-assembly) @ sap-network-monitor-server ---
[INFO] Reading assembly descriptor: src/main/assembly/assembly.xml
[INFO] Building tar: /Users/bin/git/SAPNetworkMonitor/server/target/sap-network-monitor-server-1.0.0-bin.tar.gz
```

## install

#### Environment

​	*Java SE Runtime Environment 8* 

​	*MySQL* 

#### how to run it?

##### Step 1

​	Install SAPNetworkMonitor client.

##### Step 2

​	Create Database in MySQL:

```sql
CREATE DATABASE `NIPING_DB` DEFAULT CHARACTER SET UTF8 COLLATE UTF8_GENERAL_CI;
```

##### Step 3

```Sh
tar -zxvf server/target/sap-network-monitor-server-1.0.0-bin.tar.gz
```

Modify MySQL connection configuration in *sap-network-monitor-server-1.0.0/conf/server.yml*

```yaml
database:
  driverClass: com.mysql.jdbc.Driver
  url: 'jdbc:mysql://localhost:3306/niping_db?useUnicode=true&characterEncoding=utf8&connectionCollation=utf8_general_ci'
  user: root
  password: 'root'
```

##### Step 4

Init tables and data in MySQL database:

```bash
cd /sap-network-monitor-server-1.0.0/bin
./start.sh migrate
```

start server:

```shell
./start.sh
```


