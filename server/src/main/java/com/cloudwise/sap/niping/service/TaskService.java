package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.common.entity.*;
import com.cloudwise.sap.niping.common.utils.KeyGeneration;
import com.cloudwise.sap.niping.dao.MonitorNiPingResultDao;
import com.cloudwise.sap.niping.dao.MonitorTaskDao;
import com.cloudwise.sap.niping.dao.TaskDao;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.functors.TruePredicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class TaskService {

    @Inject
    private TaskDao taskDao;
    @Inject
    private MonitorTaskDao monitorTaskDao;
    @Inject
    private MonitorNiPingResultDao monitorNiPingResultDao;

    @Inject
    private ObjectMapper objectMapper;

    public static <O> O subtract(final Iterable<? extends O> a, final Iterable<? extends O> b) {
        final ArrayList<O> list = new ArrayList<O>();
        final Predicate<O> p = TruePredicate.truePredicate();
        final HashBag<O> bag = new HashBag<O>();
        for (final O element : b) {
            if (p.evaluate(element)) {
                bag.add(element);
            }
        }
        for (final O element : a) {
            if (!bag.remove(element, 1)) {
                return element;
            }
        }
        return null;
    }

    public Optional<MonitorJob> getNextJob(String monitorId, List<String> runningTaskIds) throws NiPingException {
        log.info("monitor {} get next job", monitorId);

        //start job
        if (CollectionUtils.isEmpty(runningTaskIds)) {
            runningTaskIds = Lists.newArrayList(new String(""));
        }
        Task task = null;
        try {
            task = taskDao.getNextStartTask(monitorId, runningTaskIds, Task.Status.enable.getStatus());
        } catch (DBIException e) {
            log.error("get monitor {} next start job error {}", monitorId, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.ServerError);
        }

        if (null != task) {
            task.setAction(MonitorJob.Action.START);
        }

        //restart job
        if (null == task) {
            try {
                task = taskDao.getNextRestartTask(monitorId, MonitorTask.Redispatcher.NeedRedispatcher.getRedispatcher(), Task.Status.enable.getStatus());
            } catch (DBIException e) {
                log.error("get monitor {} next restart job error {}", monitorId, ExceptionUtils.getMessage(e));
                throw new NiPingException(NiPingException.Exception.ServerError);
            }
            if (null != task) {
                task.setAction(MonitorJob.Action.RESTART);
                redispatcheredTask(task.getTaskId(), monitorId);
            }
        }
        //stop job
        if (null == task) {
            if (CollectionUtils.isNotEmpty(runningTaskIds)) {
                List<String> taskIds = null;
                try {
                    taskIds = taskDao.getAllTaskIdsInRunningTaskIds(monitorId, runningTaskIds, Task.Status.enable.getStatus());
                } catch (DBIException e) {
                    log.error("get monitor {} next stop job: get all taskIds in runningTaskIds error: {}", monitorId, ExceptionUtils.getMessage(e));
                    throw new NiPingException(NiPingException.Exception.ServerError);
                }
                String stopId = subtract(runningTaskIds, taskIds);
                if (StringUtils.isNotEmpty(stopId)) {
                    task = Task.builder().taskId(stopId).build();
                    task.setAction(MonitorJob.Action.STOP);
                }
            }
        }

        if (null == task) {
            return Optional.empty();
        }

        try {
            Optional<MonitorJob> job = convertTask(task);
            if (log.isDebugEnabled() && job.isPresent()) {
                log.debug("monitor {} get next job with running task ids {} job {}", monitorId, Arrays.toString(runningTaskIds.toArray(new String[]{})), job.get());
            }
            return job;
        } catch (IOException e) {
            log.error("get monitor next job: convert task {} error: {}", task, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.ServerError);
        }
    }

    private Optional<MonitorJob> convertTask(Task task) throws IOException {
        if (task == null) {
            return null;
        }
        JobDesc jobDesc = null;
        if (null != task.getConfigJson()) {
            jobDesc = objectMapper.readValue(task.getConfigJson(), JobDesc.class);
        }
        long lastModifiedTime = 0;
        Date lastModifiedDate = task.getModifiedTime();
        if (null != lastModifiedDate) {
            lastModifiedTime = lastModifiedDate.getTime();
        }
        return Optional.ofNullable(MonitorJob.builder()
                .monitorId(task.getMonitorId())
                .taskId(task.getTaskId())
                .interval(task.getInterval())
                .actionType(task.getAction().getValue())
                .jobDesc(jobDesc)
                .modifiedTime(lastModifiedTime)
                .build());
    }

    public void saveTask(Task task) throws NiPingException {
        Date currentDate = new Date();
        try {
            if (StringUtils.isBlank(task.getTaskId())) {

                task.setTaskId(KeyGeneration.getKey());
                task.setCreationTime(currentDate);
                task.setModifiedTime(currentDate);
                task.setStatus(Task.Status.enable.getStatus());
                taskDao.insertTask(task);
                log.info("task {} saved", task);
            } else {
                task.setModifiedTime(currentDate);
                taskDao.updateTask(task);

                //task update needredispatch
                monitorTaskDao.updateMonitorTaskRedispatcher(task.getTaskId(), MonitorTask.Redispatcher.NeedRedispatcher.getRedispatcher());
                log.info("task {} modified", task);
            }
        }
        catch (DBIException e) {
            log.error("save task {} error: {}", task, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void enableTask(String taskId) throws NiPingException {
        try {
            taskDao.updateTaskStatus(taskId, Task.Status.enable.getStatus(), new Date(System.currentTimeMillis()));
            log.info("task {} enabled", taskId);
        }
        catch (DBIException e) {
            log.error("enable task {} error: {}", taskId, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void disableTask(String taskId) throws NiPingException {
        try {
            taskDao.updateTaskStatus(taskId, Task.Status.disable.getStatus(), new Date(System.currentTimeMillis()));
            monitorTaskDao.removeMonitorTask(taskId);
            log.info("task {} disabled", taskId);
        }
        catch (DBIException e) {
            log.error("disable task {} error: {}", taskId, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void deleteTask(String taskId) throws NiPingException {
        try {
            taskDao.updateTaskStatus(taskId, Task.Status.deleted.getStatus(), new Date(System.currentTimeMillis()));
            monitorTaskDao.removeMonitorTask(taskId);
            log.info("task {} deleted", taskId);
        }
        catch (DBIException e) {
            log.error("delete task {} error: {}", taskId, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void assignTask(List<String> monitorIds, String taskId) throws NiPingException {
        try {
            monitorTaskDao.insertMonitorTask(monitorIds, taskId);
        }
        catch (DBIException e) {
            log.error("assign task {} to monitor {} error: {}", taskId,  Arrays.toString(monitorIds.toArray(new String[]{})), ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
        log.info("task {} has been assigned to monitors {}", taskId, Arrays.toString(monitorIds.toArray(new String[]{})));
    }

    public void redispatcheredTask(String taskId, String monitorId) throws NiPingException {
        try {
            monitorTaskDao.updateMonitorTaskRedispatcher(taskId, monitorId, MonitorTask.Redispatcher.NoNeedRedispatcher.getRedispatcher());
        }
        catch (DBIException e) {
            log.error("set redispatch status: task {} monitor {} error: {}", taskId, monitorId, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
    }

    public void saveMonitorNiPingResult(MonitorNiPingResult monitorNiPingResult) throws NiPingException {
        Date currentDate = new Date();
        monitorNiPingResult.setCreationTime(currentDate);
        monitorNiPingResult.setModifiedTime(currentDate);
        try {
            monitorNiPingResultDao.saveMonitorNiPingResult(monitorNiPingResult);
        }
        catch (DBIException e) {
            log.error("save monitor niping result {} error: {}", monitorNiPingResult, ExceptionUtils.getMessage(e));
            throw new NiPingException(NiPingException.Exception.DBError);
        }
        log.info("monitor NiPing result {} saved", monitorNiPingResult);
    }
}