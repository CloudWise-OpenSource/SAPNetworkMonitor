package com.cloudwise.sap.niping.common.vo;

import lombok.Data;

@Data
public class MonitorJob {
	public static final int ACTION_STOP = 0;
	public static final int ACTION_START = 1;
	public static final int ACTION_RESTART = 2;
	private String monitorId;
	private String taskId;
	private int interval;
	private int actionType;
	private JobDesc jobDesc;
	private long modifiedTime;
}
