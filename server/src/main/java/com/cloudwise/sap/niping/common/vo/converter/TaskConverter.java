package com.cloudwise.sap.niping.common.vo.converter;

import com.cloudwise.sap.niping.common.entity.JobDesc;
import com.cloudwise.sap.niping.common.vo.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskConverter {

    @Inject
    private ObjectMapper jsonMapper;
    public Optional<List<Task>> convert(Optional<List<com.cloudwise.sap.niping.common.entity.Task>> tasks) {

        Optional<List<Task>> taskVOOptional = Optional.empty();
        if (tasks.isPresent()) {
            List<com.cloudwise.sap.niping.common.entity.Task> tasksList = tasks.get();
            List<Task> taskVOList = new ArrayList<>(tasksList.size());
            taskVOOptional = Optional.ofNullable(taskVOList);

            StringBuilder monitorIdsBuilder = null;
            StringBuilder monitorNamesBuilder = null;
            Task taskVO = null;

            boolean available = true;
            boolean allnotavailable = true;

            for (com.cloudwise.sap.niping.common.entity.Task task: tasks.get()) {
                if (null ==  taskVO || null == task.getTaskId() || !task.getTaskId().equals(taskVO.getTaskId())) {
                    try {
                        taskVO = taskVO.builder()
                                .taskId(task.getTaskId())
                                .name(task.getName())
                                .interval(task.getInterval())
                                .monitorIds(task.getMonitorId())
                                .jobDesc(jsonMapper.readValue(task.getConfigJson(), JobDesc.class))
                                .build();

                        if (com.cloudwise.sap.niping.common.entity.Task.Status.disable.getStatus() == task.getStatus()) {
                            taskVO.setStatus(Task.Status.disable.getStatus());
                        }
                        else {
                            taskVO.setStatus(1);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    setMonitorIds(taskVOList, monitorIdsBuilder, monitorNamesBuilder, available, allnotavailable);
                    available = true;
                    allnotavailable = true;
                    monitorIdsBuilder = new StringBuilder();
                    monitorNamesBuilder = new StringBuilder();

                    if (null != task.getMonitorId()) {
                        monitorIdsBuilder.append(task.getMonitorId()).append(",");
                        monitorNamesBuilder.append(task.getMonitorName()).append(",");
                        if (task.getResultId() == null) {
                            available = false;
                        }
                        else {
                            allnotavailable = false;
                        }
                    }
                    taskVOList.add(taskVO);
                }
                else {
                    monitorIdsBuilder.append(task.getMonitorId()).append(",");
                    monitorNamesBuilder.append(task.getMonitorName()).append(",");
                    if (task.getResultId() == null) {
                        available = false;
                    }
                    else {
                        allnotavailable = false;
                    }
                }
            }
            setMonitorIds(taskVOList, monitorIdsBuilder, monitorNamesBuilder, available, allnotavailable);
        }

        return taskVOOptional;
    }

    private void setMonitorIds(List<Task> taskVOList, StringBuilder monitorIdsBuilder, StringBuilder monitorNamesBuilder, boolean available, boolean allnotavailable ) {
        if (taskVOList.size() > 0) {
            Task task = taskVOList.get(taskVOList.size() - 1);
            if (null != monitorIdsBuilder) {
                String monitorIdsString = monitorIdsBuilder.toString();
                String monitorNamesString = monitorNamesBuilder.toString();
                if (StringUtils.isNotEmpty(monitorIdsString)) {
                    task.setMonitorIds(monitorIdsString.substring(0, monitorIdsString.length() - 1));
                    task.setMonitorNames(monitorNamesString.substring(0, monitorNamesString.length() - 1));
                }
            }

            if (task.getStatus() != Task.Status.disable.getStatus()) {
                task.setAvailable(true);
                if (available) {task.setStatus(Task.Status.available.getStatus());}
                else if (allnotavailable) {task.setStatus(Task.Status.allnotavailable.getStatus());}
                else {task.setStatus(Task.Status.partavailable.getStatus());}
            }
            else {
                task.setAvailable(false);
            }
        }
    }

    public Optional<com.cloudwise.sap.niping.common.entity.Task> convertTaskVO(Optional<Task> taskVOOptional) {
        com.cloudwise.sap.niping.common.entity.Task task = null;
        if (taskVOOptional.isPresent()) {
            Task taskVO = taskVOOptional.get();
            task = com.cloudwise.sap.niping.common.entity.Task.builder()
                    .taskId(taskVO.getTaskId())
                    .name(taskVO.getName())
                    .interval(taskVO.getInterval())
                    .jobDesc(taskVO.getJobDesc())
                    .build();
        }
        return Optional.ofNullable(task);
    }
}