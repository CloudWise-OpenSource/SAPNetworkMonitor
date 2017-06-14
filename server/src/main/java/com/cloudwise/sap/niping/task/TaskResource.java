package com.cloudwise.sap.niping.task;

import com.cloudwise.sap.niping.auth.BasicAuthUser;
import com.cloudwise.sap.niping.common.constant.ReturnConstant;
import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.service.TaskService;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.NotEmpty;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TaskResource {

    @Inject
    private TaskService taskService;

    @POST
    @Path("/task")
    public RestfulReturnResult saveTask(@Auth BasicAuthUser user, Task task) {
        task.setAccountId(user.getAccountId());
        taskService.saveTask(task);
        return new RestfulReturnResult(ReturnConstant.SUCCESS, "success", null);
    }

    @POST
    @Path("/task/{taskId}/monitor")
    public RestfulReturnResult assignTask(@Auth BasicAuthUser user, @PathParam("taskId") String taskId, @NotEmpty @Valid List<String> monitorIds) {
        taskService.assignTask(monitorIds, taskId);
        return new RestfulReturnResult(ReturnConstant.SUCCESS, "success", null);
    }
}
