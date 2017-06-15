package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.SapConfiguration;
import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.dao.MonitorDao;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.RetryPolicy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.HEAD;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cloudwise.sap.niping.common.constant.Result.DBError;

@Slf4j
public class MonitorService {

    @Inject
    private MonitorDao monitorDao;
    @Inject
    private SapConfiguration sapConfig;

    Set<String> lastIntervalMonitors = Sets.newConcurrentHashSet();
    Set<String> intervalMonitors = Sets.newConcurrentHashSet();
    private static final Object monitorsLock = new Object();

    @PostConstruct
    public void monitorsHeartbeat() {

        new Thread(() -> {
            try {
                Failsafe.with(retryPolicy)
                        .onSuccess(result -> log.info("all monitors setted to inactive"))
                        .onRetry(e -> log.error("monitors heartbeat thread: inactive all monitors error, retry......"))
                        .run(() -> inactiveAllMonitors());
            } catch (FailsafeException e) {
                log.error("monitors heartbeat thread: inactive all monitors fail.");
            }

            log.info("start monitor heartbeat thread......");
            int heartbeatLostTime = sapConfig.getMonitorConfiguration().getLostTime();
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
                //in intervalMonitors not in lastIntervalMonitors; set active
                List<String> activeMonitors = intervalMonitors.stream().filter((id) -> {
                    return !lastIntervalMonitors.contains(id);
                }).collect(Collectors.toList());
                //in lastIntervalMonitors not in intervalMonitors; set inactive
                List<String> inactiveMonitors = lastIntervalMonitors.stream().filter((id) -> {
                    return !intervalMonitors.contains(id);
                }).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(activeMonitors)) {
                    Failsafe.with(retryPolicy)
                            .onSuccess(connection -> log.info("monitors {} setted to active", Arrays.toString(activeMonitors.toArray(new String[]{}))))
                            .onRetry(e -> log.error("monitors heartbeat thread: active monitors {} error, retry......", Arrays.toString (activeMonitors.toArray(new String[]{}))))
                            .onComplete((cxn, failure) -> log.error("monitors heartbeat thread: active monitors {} failed after retries", Arrays.toString(activeMonitors.toArray(new String[]{}))))
                            .get(() -> modifyMonitorsStatus(activeMonitors, Monitor.Status.active));
                }
                if (CollectionUtils.isNotEmpty(inactiveMonitors)) {
                    Failsafe.with(retryPolicy)
                            .onSuccess(connection -> log.info("monitors {} setted to inactive", Arrays.toString(inactiveMonitors.toArray(new String[]{}))))
                            .onRetry(e -> log.error("monitors heartbeat thread: inactive monitors {} error, retry......", Arrays.toString(inactiveMonitors.toArray(new String[]{}))))
                            .onComplete((cxn, failure) -> log.error("monitors heartbeat thread: inactive monitors {} failed after retries", Arrays.toString(inactiveMonitors.toArray(new String[]{}))))
                            .get(() -> modifyMonitorsStatus(inactiveMonitors, Monitor.Status.inactive));
                }

                synchronized (monitorsLock) {
                    lastIntervalMonitors = intervalMonitors;
                    intervalMonitors = Sets.newConcurrentHashSet();
                }
            }, heartbeatLostTime, heartbeatLostTime, TimeUnit.SECONDS);
            log.info("monitor heartbeat thread started");
        }).start();
    }

    public void heartbeat(Monitor monitor) {
        synchronized (monitorsLock) {
            String monitorId = monitor.getMonitorId();
            intervalMonitors.add(monitorId);
            log.debug("received monitor {} heartbeat.", monitorId);
        }
    }

    public void saveMonitor(Monitor monitor) throws NiPingException {
        String monitorId = monitor.getMonitorId();

        Date currentDate = new Date();
        String nipingT = monitor.getNipingT();
        monitor.setModifiedTime(currentDate);

        try {
            if (monitorDao.countMonitor(monitorId) == 0) {
                monitor.setCreationTime(currentDate);
                monitor.setStatus(Monitor.Status.active.getStatus());
                monitorDao.insertMonitor(monitor);
                log.debug("monitor {} saved", monitor);

            } else if (StringUtils.isNoneBlank(nipingT)) {
                monitorDao.updateMonitorNiping(monitor);
                log.debug("monitor {} modified", monitor);
            }
        } catch (DBIException e) {
            log.error("monitors: save monitor {} error: {}", monitor, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
    }

    public boolean inactiveAllMonitors() throws NiPingException {
        try {
            monitorDao.updateAllMonitorsStatus(Monitor.Status.inactive.getStatus(), new Date(System.currentTimeMillis()));
            log.debug("all monitors setted to inactive");
        } catch (DBIException e) {
            log.error("inactive all monitors error: {}", ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
        return true;
    }

    public boolean modifyMonitorsStatus(List<String> monitorIds, Monitor.Status status) throws NiPingException {
        if (CollectionUtils.isNotEmpty(monitorIds)) {
            try {
                monitorDao.updateMonitorsStatus(monitorIds, status.getStatus(), new Date(System.currentTimeMillis()));
                log.error("modify monitors {} status {}", Arrays.toString(monitorIds.toArray(new String[]{})), status);
            } catch (DBIException e) {
                log.error("modify monitors {} status {} error: {}", Arrays.toString(monitorIds.toArray(new String[]{})), status,
                        ExceptionUtils.getMessage(e));
                throw new NiPingException(DBError);
            }
        }
        return true;
    }

    RetryPolicy retryPolicy = new RetryPolicy()
            .retryOn(NiPingException.class)
            .withDelay(5, TimeUnit.SECONDS)
            .withMaxRetries(3);
}