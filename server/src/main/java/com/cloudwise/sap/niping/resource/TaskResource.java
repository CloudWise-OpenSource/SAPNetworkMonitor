package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.common.vo.converter.TaskConverter;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import com.cloudwise.sap.niping.service.MonitorService;
import com.cloudwise.sap.niping.service.TaskService;
import com.cloudwise.sap.niping.view.TaskAddView;
import com.cloudwise.sap.niping.view.TaskEditView;
import com.cloudwise.sap.niping.view.TaskListView;
import io.dropwizard.jersey.sessions.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    TaskConverter taskConverter;
    @Inject
    private TaskService taskService;

    @Inject
    private MonitorService monitorService;

    @GET
    @Path("/addTask")
    @Produces(MediaType.TEXT_HTML)
    public TaskAddView getTaskCreateView(@Session HttpSession session) {
        try {
            return new TaskAddView(SUCCESS, monitorService.listAllMonitors(NiPingAuthFilter.getAccountId(session)).orElse(null));
        } catch (NiPingException e) {
            return new TaskAddView(e, null);
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public TaskListView listTasks(@Session HttpSession session) {
        try {
            TaskListView taskListView = new TaskListView(SUCCESS, taskConverter.convert(taskService.listTasksForListPage(NiPingAuthFilter
                    .getAccountId(session))).orElse(null));
            return taskListView;
        } catch (NiPingException e) {
            return new TaskListView(e, null);
        }
    }

    @GET
    @Path("/{taskId}")
    @Produces(MediaType.TEXT_HTML)
    public TaskEditView getTask(@Session HttpSession session, @PathParam("taskId") String taskId) {
        try {
            return new TaskEditView(SUCCESS, taskService.getTask(NiPingAuthFilter.getAccountId(session), taskId));
        } catch (NiPingException e) {
            return new TaskEditView(e, null);
        }
    }

    @POST
    @Path("/task")
    public RestfulReturnResult saveTask(@Session HttpSession session, com.cloudwise.sap.niping.common.vo.Task taskVO) {
        Optional<Task> taskOptional = taskConverter.convertTaskVO(Optional.ofNullable(taskVO));
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setAccountId(NiPingAuthFilter.getAccountId(session));
            try {
                log.info("user {} save task {}", task);
                String taskId = taskService.saveTask(task);

                String selectMonitorIdString = taskVO.getSelectMonitorIdString();
                ArrayList<String> taskIds = Lists.newArrayList();
                if (StringUtils.isNotBlank(selectMonitorIdString)) {
                    taskIds = new ArrayList<String>(Arrays.asList(selectMonitorIdString.split(",")));
                }
                taskService.assignTask(taskIds, taskId);
            } catch (NiPingException e) {
                return new RestfulReturnResult(e, null);
            }
        }
        return new RestfulReturnResult(SUCCESS, null);
    }

    @PUT
    @Path("/{taskId}/{status}")
    public RestfulReturnResult changeTaskStatus(@Session HttpSession session, @PathParam("taskId") String taskId, @PathParam("status")
            String status) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        try {
            if (status.equals(TaskStatusChange.disable.toString())) {
                taskService.disableTask(accountId, taskId);
            } else if (status.equals(TaskStatusChange.enable.toString())) {
                //              log.info("user {} enable task {}", user , task);
                taskService.enableTask(accountId, taskId);
            }
        } catch (NiPingException e) {
            return new RestfulReturnResult(e, null);
        }
        return new RestfulReturnResult(SUCCESS, null);
    }

    @DELETE
    @Path("/{taskId}")
    public RestfulReturnResult deleteTask(@Session HttpSession session, @PathParam("taskId") String taskId) {
        try {
            //            log.info("user {} enable task {}", user, task);
            taskService.deleteTask(NiPingAuthFilter.getAccountId(session), taskId);
        } catch (NiPingException e) {
            return new RestfulReturnResult(e, null);
        }
        return new RestfulReturnResult(SUCCESS, null);
    }

    enum TaskStatusChange {
        enable, disable;
    }
}
