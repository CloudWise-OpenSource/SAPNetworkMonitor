package com.cloudwise.sap.niping.task;

import com.cloudwise.sap.niping.auth.BasicAuthUser;
import com.cloudwise.sap.niping.common.constant.ReturnConstant;
import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.exception.NiPingException;
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
        try {
            taskService.saveTask(task);
        } catch (NiPingException e) {
            return new RestfulReturnResult(e.getCode(), e.getErrorMessage(), null);
        }
        return new RestfulReturnResult(ReturnConstant.SUCCESS, "success", null);
    }

    @POST
    @Path("/task/{taskId}/monitor")
    public RestfulReturnResult assignTask(@Auth BasicAuthUser user, @PathParam("taskId") String taskId, @NotEmpty @Valid List<String> monitorIds) {

        try {
            taskService.assignTask(monitorIds, taskId);
        } catch (NiPingException e) {
            return new RestfulReturnResult(e.getCode(), e.getErrorMessage(), null);
        }
        return new RestfulReturnResult(ReturnConstant.SUCCESS, "success", null);
    }
}
