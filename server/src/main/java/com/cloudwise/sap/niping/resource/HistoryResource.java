package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.*;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import com.cloudwise.sap.niping.service.MonitorResultService;
import com.cloudwise.sap.niping.service.MonitorService;
import com.cloudwise.sap.niping.service.TaskService;
import com.cloudwise.sap.niping.view.HistoryListView;
import com.cloudwise.sap.niping.view.HistoryView;
import io.dropwizard.jersey.sessions.Session;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.cloudwise.sap.niping.common.constant.Result.*;

@Slf4j
@Path("/api/history")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class HistoryResource {

    @Inject
    private TaskService taskService;

    @Inject
    private MonitorService monitorService;

    @Inject
    private MonitorResultService monitorResultService;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public HistoryView gotoHistory(@Session HttpSession session) {
        return new HistoryView(Result.SUCCESS, null);
    }

    @GET
    @Path("/tasks")
    public RestfulReturnResult listTasks(@Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<Task> tasks = null;
        try {
            tasks = taskService.listTasks(accountId).orElseGet(null);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, tasks);
    }

    @GET
    @Path("/tasks/{taskId}/monitors")
    public RestfulReturnResult listMonitors(@PathParam("taskId") String taskId, @QueryParam("country") String country, @QueryParam("province") String province, @QueryParam("city") String city, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<Monitor> monitors = null;
        try {
            monitors = monitorResultService.listMonitors(accountId, taskId, country, province, city);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, monitors);
    }

    @GET
    @Path("/country")
    public RestfulReturnResult listCountries(@Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<String> countries = null;
        try {
            countries = monitorResultService.listCountries(accountId);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, countries);
    }


    @GET
    @Path("/{country}/province")
    public RestfulReturnResult listProvinces(@PathParam("country") String country, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<String> provinces = null;
        try {
            provinces = monitorResultService.listProvinces(accountId, country);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, provinces);
    }

    @GET
    @Path("/{country}/{province}/city")
    public RestfulReturnResult listCities(@PathParam("country") String country, @PathParam("province") String province, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<String> cities = null;
        try {
            cities = monitorResultService.listCities(accountId, country, province);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, cities);
    }

    @GET
    @Path("/data/{taskId}/{monitorId}")
    public RestfulReturnResult getHistoryData(@PathParam("taskId") String taskId, @PathParam("monitorId") String monitorId,  @QueryParam("time") long time, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        List<String> cities = null;
        HistoryData historyData = null;
        try {
            List<Metrics> trMetrics = monitorResultService.listTRMetrics(accountId, taskId, monitorId, time);
            List<Metrics> avMetrics = monitorResultService.listAVMetrics(accountId, taskId, monitorId, time);

            AnalysisUsable performance = null;
            AnalysisUsable stability = null;
            AnalysisUsable idleTimeout = null;

            Optional<Map<Integer, Map<Boolean, Long>>> mapOptional = monitorResultService.getUsable(accountId, taskId, monitorId, time);

            if (mapOptional.isPresent()) {
                Map<Integer, Map<Boolean, Long>> map = mapOptional.get();
                performance = getAnalysisUsable(map, MonitorNiPingResult.Type.PERFORMANCE.getValue());
                stability = getAnalysisUsable(map, MonitorNiPingResult.Type.STABILITY.getValue());
                idleTimeout = getAnalysisUsable(map, MonitorNiPingResult.Type.IDLE_TIMEOUT.getValue());
            }

            historyData = HistoryData.builder()
                    .trMetrics(trMetrics)
                    .avMetrics(avMetrics)
                    .performance(performance)
                    .stability(stability)
                    .idleTimeout(idleTimeout)
                    .build();

        } catch (NiPingException e) {
            e.printStackTrace();
            return new RestfulReturnResult(Error, null);
        }
        return new RestfulReturnResult(SUCCESS, historyData);
    }

    @GET
    @Path("/results/{taskId}/{monitorId}")
    @Produces(MediaType.TEXT_HTML)
    public HistoryListView getHistoryData(@PathParam("taskId") String taskId, @PathParam("monitorId") String monitorId, @QueryParam("pageNo") Long pageNo, @QueryParam("pageSize") Long pageSize, @QueryParam("type") Integer type, @QueryParam("time") long time, @Session HttpSession session) {
        String accountId = NiPingAuthFilter.getAccountId(session);
        Page<MonitorNiPingResult> results = null;
        try {
            results = monitorResultService.list(accountId, taskId, monitorId, time, type, pageNo, pageSize);
        } catch (NiPingException e) {
            e.printStackTrace();
            return new HistoryListView(Error, null);
        }
        return new HistoryListView(Result.SUCCESS, results);
    }

    private AnalysisUsable getAnalysisUsable(Map<Integer, Map<Boolean, Long>> map, int type) {
        AnalysisUsable performance = null;
        if (map.containsKey(type)) {
            Map<Boolean, Long> mapResult = map.get(type);
            performance = AnalysisUsable.builder()
                    .usable(mapResult.get(Boolean.TRUE))
                    .notUsable(mapResult.get(Boolean.FALSE))
                    .build();
        }
        return performance;
    }
}