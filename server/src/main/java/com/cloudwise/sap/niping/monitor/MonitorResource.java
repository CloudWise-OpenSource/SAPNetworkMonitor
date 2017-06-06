package com.cloudwise.sap.niping.monitor;

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
import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.vo.JobDesc;
import com.cloudwise.sap.niping.common.vo.MonitorJob;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;

@Path("/api/monitors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MonitorResource {

	/**
	 * Agent和Server之间的心跳，可以1分钟或更长时间一次，传回Monitor的信息，返回MonitorJob信息
	 * 
	 * @param monitorId
	 * @param monitor
	 * @return
	 */
	@POST
	@Path("/monitor/{monitorId}/heartbeat")
	public RestfulReturnResult heartbeat(@PathParam("monitorId") String monitorId, @NotNull @Valid Monitor monitor) {
		return null;
	}

	public static void main(String[] args) {
		MonitorJob mj = new MonitorJob();
		mj.setActionType(MonitorJob.ACTION_RESTART);
		mj.setInterval(2);
		mj.setModifiedTime(System.currentTimeMillis());
		mj.setMonitorId("mid1");
		mj.setTaskId("taskId1");
		JobDesc jobDesc = new JobDesc();
		jobDesc.setRouter("/H/10.0.1.1/H/192.168.1.1");
		mj.setJobDesc(jobDesc);
		RestfulReturnResult r = new RestfulReturnResult(ReturnConstant.SUCCESS, null, mj);
		System.out.println(JSON.toJSONString(r));

		Monitor m = new Monitor();
		m.setMonitorId("mid1");
		m.setCountry("中国");
		m.setArea("华北");
		m.setProvince("河北");
		m.setCity("三河");
		m.setIp("10.0.1.112");
		m.setIsp("中国电信");
		m.setName("XXX公司三河财务部");
		m.setRunningTaskIds(new String[] { "task6", "task2" });
		System.out.println(JSON.toJSONString(m));

	}
}
