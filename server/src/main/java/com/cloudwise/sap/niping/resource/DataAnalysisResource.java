package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import com.cloudwise.sap.niping.service.MonitorResultService;
import com.cloudwise.sap.niping.view.AnalysisListView;
import com.cloudwise.sap.niping.view.AnalysisView;
import io.dropwizard.jersey.sessions.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/api/analysis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DataAnalysisResource {

    @Inject
    private MonitorResultService monitorResultService;


    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public AnalysisView result(@Session HttpSession session) {
        try {
            String accountId = NiPingAuthFilter.getAccountId(session);
            return new AnalysisView(SUCCESS, monitorResultService.listTasks(accountId));
        } catch (NiPingException e) {
            return new AnalysisView(e, null);
        }
    }

    @GET
    @Path("/list")
    @Produces(MediaType.TEXT_HTML)
    public AnalysisListView result(@QueryParam("taskId") String taskId, @QueryParam("time") Long time, @Session HttpSession session) {
        try {
            String accountId = NiPingAuthFilter.getAccountId(session);

            if (StringUtils.isEmpty(taskId)) {
                return new AnalysisListView(SUCCESS, null);
            }

            if (null != time && time != 0) {
                time = System.currentTimeMillis() - time * 60 * 1000;
            } else {
                time = null;
            }
            return new AnalysisListView(SUCCESS, monitorResultService.listByTaskId(accountId, taskId, time));
        } catch (NiPingException e) {
            return new AnalysisListView(e, null);
        }
    }
}