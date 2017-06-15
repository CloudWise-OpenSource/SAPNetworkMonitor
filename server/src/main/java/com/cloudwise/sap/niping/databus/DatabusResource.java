package com.cloudwise.sap.niping.databus;

import com.cloudwise.sap.niping.auth.OAuthUser;
import com.cloudwise.sap.niping.common.constant.ReturnConstant;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.service.TaskService;
import io.dropwizard.auth.Auth;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/databus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DatabusResource {

    @Inject
    private TaskService taskService;

    @POST
    @Path("/monitor/{monitorId}/result")
    public RestfulReturnResult result(@Auth OAuthUser user, @PathParam("monitorId") String monitorId, @NotNull @Valid MonitorNiPingResult monitorNiPingResult) {
        monitorNiPingResult.setAccountId(user.getAccountId());
        monitorNiPingResult.setMonitorId(monitorId);
        try {
            taskService.saveMonitorNiPingResult(monitorNiPingResult);
        } catch (NiPingException e) {
            return new RestfulReturnResult(e.getCode(), e.getErrorMessage(), null);
        }
        return new RestfulReturnResult(ReturnConstant.SUCCESS, "success", null);
    }
}
