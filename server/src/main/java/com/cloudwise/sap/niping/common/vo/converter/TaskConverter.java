package com.cloudwise.sap.niping.common.vo.converter;

import com.cloudwise.sap.niping.common.entity.JobDesc;
import com.cloudwise.sap.niping.common.vo.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskConverter {

    @Inject
    private ObjectMapper jsonMapper;
    public Optional<List<Task>> convert(Optional<List<com.cloudwise.sap.niping.common.entity.Task>> tasks) {

        Optional<List<Task>> taskVOOptional = Optional.empty();
        if (tasks.isPresent()) {

            List<com.cloudwise.sap.niping.common.entity.Task> tasksList = tasks.get();

            if (CollectionUtils.isNotEmpty(tasksList)) {
                Map<String, List<com.cloudwise.sap.niping.common.entity.Task>> tasksMap = tasksList.stream().collect(Collectors.groupingBy((task)->{return task.getTaskId();}, Collectors.toList()));
                List<Task> tasksVOList = new ArrayList<>(tasksMap.size());
                tasksMap.forEach((key, value) -> {
                    com.cloudwise.sap.niping.common.entity.Task task = value.get(0);
                    try {
                        Task taskVO = Task.builder()
                                .taskId(task.getTaskId())
                                .name(task.getName())
                                .interval(task.getInterval())
                                .jobDesc(jsonMapper.readValue(task.getConfigJson(), JobDesc.class))
                                .build();
                        taskVO.setMonitorIds(value.stream().map(v->v.getMonitorId()).filter((e) -> e!=null).collect(Collectors.joining(",")));
                        taskVO.setMonitorNames(value.stream().map(v->v.getMonitorName()).filter((e) -> e!=null).collect(Collectors.joining(",")));
                        if (task.getStatus() == Task.Status.disable.getStatus()) {
                            taskVO.setStatus(Task.Status.disable.getStatus());
                        }
                        else {
                            List<com.cloudwise.sap.niping.common.entity.Task> withResultTasks = value.stream().filter((t) -> {return t.getResultMonitorId() != null;}).collect(Collectors.toList());
                            long withResultTasksCount = withResultTasks.size();
                            if (withResultTasksCount == 0) {
                                taskVO.setStatus(Task.Status.available.getStatus());
                            }
                            else {
                                List<com.cloudwise.sap.niping.common.entity.Task> errorTasks = withResultTasks.stream().filter((t) -> {return t.getErrno() != 0;}).collect(Collectors.toList());
                                if (errorTasks.size() == withResultTasks.size()) {
                                    taskVO.setStatus(Task.Status.allnotavailable.getStatus());
                                }
                                else if (errorTasks.size() == 0) {
                                    taskVO.setStatus(Task.Status.available.getStatus());
                                }
                                else {
                                    taskVO.setStatus(Task.Status.partavailable.getStatus());
                                }
                            }
                        }
                        tasksVOList.add(taskVO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
                taskVOOptional = Optional.ofNullable(tasksVOList);
            }
        }
        return taskVOOptional;
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