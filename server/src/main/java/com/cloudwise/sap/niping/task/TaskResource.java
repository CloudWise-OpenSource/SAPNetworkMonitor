package com.cloudwise.sap.niping.task;

import com.cloudwise.sap.niping.common.vo.converter.TaskConverter;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.service.TaskService;
import com.cloudwise.sap.niping.view.TasksView;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    private TaskService taskService;

//    @POST
//    @Path("/task")
//    public RestfulReturnResult saveTask(@Auth BasicAuthUser user, Task task) {
//        task.setAccountId(user.getAccountId());
//        try {
//            log.info("user {} save task {}", user, task);
//            taskService.saveTask(task);
//        } catch (NiPingException e) {
//            return new RestfulReturnResult(e, null);
//        }
//        return new RestfulReturnResult(SUCCESS, null);
//    }
//
//    @POST
//    @Path("/task/{taskId}/monitor")
//    public RestfulReturnResult assignTask(@Auth BasicAuthUser user, @PathParam("taskId") String taskId, @NotEmpty @Valid List<String>
//            monitorIds) {
//
//        try {
//            log.info("user {} assign task {} to monitors {}", user, taskId, Arrays.toString(monitorIds.toArray(new String[]{})));
//            taskService.assignTask(monitorIds, taskId);
//        } catch (NiPingException e) {
//            return new RestfulReturnResult(e, null);
//        }
//        return new RestfulReturnResult(SUCCESS, null);
//    }


    @Inject
    TaskConverter taskConverter;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public TasksView listTasks() {
        try {
            return new TasksView(SUCCESS, taskConverter.convert(taskService.listTasks("a1")).orElse(null));
        } catch (NiPingException e) {
            return new TasksView(e, null);
        }
    }
}
