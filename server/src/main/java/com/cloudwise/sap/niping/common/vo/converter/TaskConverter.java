package com.cloudwise.sap.niping.common.vo.converter;

import com.cloudwise.sap.niping.common.entity.JobDesc;
import com.cloudwise.sap.niping.common.vo.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            String monitorIds;
            List<com.cloudwise.sap.niping.common.entity.Task> tasksList = tasks.get();
            List<Task> taskVOList = new ArrayList<>(tasksList.size());
            taskVOOptional = Optional.ofNullable(taskVOList);

            StringBuilder monitorIdsBuilder = null;
            Task taskVO = null;

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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setMonitorIds(taskVOList, monitorIdsBuilder);
                    if (null != task.getMonitorId()) {
                        monitorIdsBuilder = new StringBuilder();
                        monitorIdsBuilder.append(task.getMonitorId()).append(",");
                    }
                    taskVOList.add(taskVO);
                }
                else {
                    monitorIdsBuilder.append(task.getMonitorId()).append(",");
                }
            }
            setMonitorIds(taskVOList, monitorIdsBuilder);
        }

        return taskVOOptional;
    }

    private void setMonitorIds(List<Task> taskVOList, StringBuilder monitorIdsBuilder) {
        if (taskVOList.size() > 1 && null != monitorIdsBuilder) {
            String monitorIdsString = monitorIdsBuilder.toString();
            taskVOList.get(taskVOList.size() - 1).setMonitorIds(monitorIdsString.substring(0, monitorIdsString.length() - 1));
        }
    }
}