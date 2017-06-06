package com.cloudwise.sap.niping.databus;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSON;
import com.cloudwise.sap.niping.common.constant.ReturnConstant;
import com.cloudwise.sap.niping.common.entity.MonitorNipingResult;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;

@Path("/api/databus")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DatabusResource {

	@POST
	@Path("/monitor/{monitorId}/result")
	public RestfulReturnResult result(@PathParam("monitorId") String monitorId,
			@NotNull @Valid MonitorNipingResult monitorNipingResult) {
		return new RestfulReturnResult(ReturnConstant.SUCCESS, "", null);
	}

	public static void main(String[] args) {
		MonitorNipingResult mnr = new MonitorNipingResult();
		mnr.setAv2(100d);
		mnr.setAvg(80d);
		mnr.setEndTime(System.currentTimeMillis());
		mnr.setStartTime(System.currentTimeMillis());
		mnr.setErrmsg("niping error result.");
		mnr.setErrno(13923);
		mnr.setMax(100d);
		mnr.setMin(30d);
		mnr.setTaskId("taskId111");
		mnr.setTr(3923d);
		mnr.setTr2(323d);
		System.out.println(JSON.toJSONString(mnr));
	}
}
