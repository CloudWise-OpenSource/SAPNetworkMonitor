package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.auth.OAuthUser;
import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.service.TaskService;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static com.cloudwise.sap.niping.common.constant.Result.MonitoridNotMatchError;
import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/databus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DatabusResource {

    @Inject
    private TaskService taskService;

    @POST
    @Path("/monitor/{monitorId}/result")
    public RestfulReturnResult result(@Auth OAuthUser user, @PathParam("monitorId") String monitorId, @NotNull @Valid MonitorNiPingResult
            monitorNiPingResult) {
        if (!monitorId.equals(monitorNiPingResult.getMonitorId())) {
            log.error("monitor id in path {} and json {} and parameter not match error.", monitorId, monitorNiPingResult.getMonitorId());
            return new RestfulReturnResult(new NiPingException(MonitoridNotMatchError), null);
        }

        monitorNiPingResult.setAccountId(user.getAccountId());
        monitorNiPingResult.setMonitorId(monitorId);
        try {
            log.info("user {} save monitor NiPing result {}", user, monitorNiPingResult);
            taskService.saveMonitorNiPingResult(monitorNiPingResult);
        } catch (NiPingException e) {
            return new RestfulReturnResult(e, null);
        }
        return new RestfulReturnResult(SUCCESS, null);
    }
}
