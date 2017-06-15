package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.SapConfiguration;
import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.dao.MonitorDao;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MonitorService {

    @Inject
    private MonitorDao monitorDao;

    @Inject
    private SapConfiguration sapConfig;

    Set<String> lastIntervalMonitors;
    Set<String> intervalMonitors;
    private static final Object monitorsLock = new Object();

    @PostConstruct
    public void monitorsHeartbeat() {
        log.info("start monitor heartbeat thread ..");
        try {
            inactiveAllMonitors();
        } catch (NiPingException e) {
            log.error("monitors Heartbeat: inactive all monitors error: {}", ExceptionUtils.getMessage(e));
        }
        log.info("inactive all monitors.");

        int heartbeatLostTime = sapConfig.getMonitorConfiguration().getLostTime();
        lastIntervalMonitors = Sets.newConcurrentHashSet();
        intervalMonitors = Sets.newConcurrentHashSet();

        ScheduledExecutorService heartBeatExcuter = Executors.newSingleThreadScheduledExecutor();
        heartBeatExcuter.scheduleAtFixedRate(() -> {
            //in intervalMonitors not in lastIntervalMonitors; set active
            List<String> activeMonitors = intervalMonitors.stream().filter((id) -> {return !lastIntervalMonitors.contains(id);}).collect(Collectors.toList());
            //in lastIntervalMonitors not in intervalMonitors; set inactive
            List<String> inactiveMonitors = lastIntervalMonitors.stream().filter((id) -> {return !intervalMonitors.contains(id);}).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(activeMonitors)) {
                try {
                    this.modifyMonitorsStatus(activeMonitors, Monitor.Status.active);
                } catch (NiPingException e) {
                    log.error("monitors Heartbeat thread: active monitors {} error: {}", activeMonitors, ExceptionUtils.getMessage(e));
                }
                log.info("monitors {} setted to active", Arrays.toString(activeMonitors.toArray(new String[]{})));
            }
            if (CollectionUtils.isNotEmpty(inactiveMonitors)) {
                try {
                    this.modifyMonitorsStatus(inactiveMonitors, Monitor.Status.inactive);
                } catch (NiPingException e) {
                    log.error("monitors Heartbeat thread: inactive monitors {} error: {}", inactiveMonitors, ExceptionUtils.getMessage(e));
                }
                log.info("monitors {} setted to inactive", Arrays.toString(inactiveMonitors.toArray(new String[]{})));
            }

            synchronized (monitorsLock) {
                lastIntervalMonitors = intervalMonitors;
                intervalMonitors = Sets.newConcurrentHashSet();
            }
        }, heartbeatLostTime, heartbeatLostTime, TimeUnit.SECONDS);
        log.info("monitor heartbeat thread started");
    }

    public void heartbeat(Monitor monitor) {
        synchronized (monitorsLock) {
            String monitorId = monitor.getMonitorId();
            intervalMonitors.add(monitorId);
            log.info("received monitor {} heartbeat.", monitorId);
        }
    }

    public void saveMonitor(Monitor monitor)  throws NiPingException {
        String monitorId = monitor.getMonitorId();

        Date currentDate = new Date();
        String nipingT = monitor.getNipingT();
        monitor.setModifiedTime(currentDate);

        try {
            if (monitorDao.countMonitor(monitorId) == 0) {
                monitor.setCreationTime(currentDate);
                monitor.setStatus(Monitor.Status.active.getStatus());
                monitorDao.insertMonitor(monitor);
                log.info("monitor {} saved", monitor);

            } else if (StringUtils.isNoneBlank(nipingT)) {
                monitorDao.updateMonitorNiping(monitor);
                log.info("monitor {} modified", monitor);
            }
        }
        catch (DBIException e) {
            log.error("monitors: save monitor {} error: {}", monitor, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void inactiveAllMonitors() throws NiPingException {
        try {
            monitorDao.updateAllMonitorsStatus(Monitor.Status.inactive.getStatus(), new Date(System.currentTimeMillis()));
            log.info("all monitors setted to inactive");
        }
        catch (DBIException e) {
            log.error("inactive all monitors error: {}", ExceptionUtils.getMessage(e));
        }
    }

    public void modifyMonitorsStatus(List<String> monitorIds, Monitor.Status status) throws NiPingException {
        if (CollectionUtils.isNotEmpty(monitorIds)) {
            try {
                monitorDao.updateMonitorsStatus(monitorIds, status.getStatus(), new Date(System.currentTimeMillis()));
            }
            catch (DBIException e) {
                log.error("modify monitors {} status {} error: {}", Arrays.toString(monitorIds.toArray(new String[]{})), status, ExceptionUtils.getMessage(e));
            }
        }
    }
}