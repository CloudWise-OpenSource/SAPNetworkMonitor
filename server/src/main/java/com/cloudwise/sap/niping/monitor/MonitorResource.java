package com.cloudwise.sap.niping.monitor;

import com.cloudwise.sap.niping.auth.OAuthUser;
import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.MonitorJob;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.service.MonitorService;
import com.cloudwise.sap.niping.service.TaskService;
import io.dropwizard.auth.Auth;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/monitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitorResource {

    @Inject
    private MonitorService monitorService;
    @Inject
    private TaskService taskService;

    /**
     * Agent和Server之间的心跳，可以1分钟或更长时间一次，传回Monitor的信息，返回MonitorJob信息
     *
     * @param monitorId
     * @param monitor
     * @return
     */
    @POST
    @Path("/monitor/{monitorId}/heartbeat")
    public RestfulReturnResult heartbeat(@Auth OAuthUser user, @PathParam("monitorId") String monitorId, @NotNull @Valid Monitor monitor) {
        monitor.setMonitorId(monitorId);
        monitor.setAccountId(user.getAccountId());

        log.info("user {} monitorId {} send heartbeat {}", user, monitorId, monitor);
        monitorService.heartbeat(monitor);
        Optional<MonitorJob> job = Optional.empty();
        try {
            monitorService.saveMonitor(monitor);
            job = taskService.getNextJob(monitorId, monitor.getRunningTaskIds());
            if (log.isInfoEnabled() && job.isPresent()) {
                log.info("user {} monitorId {} get next job {}", user, monitorId, job.get());
            }
        } catch (NiPingException e) {
            return new RestfulReturnResult(e, job.orElse(null));
        }
        return new RestfulReturnResult(SUCCESS, job.orElse(null));
    }
}