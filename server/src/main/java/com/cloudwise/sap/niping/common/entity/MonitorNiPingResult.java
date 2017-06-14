package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorNiPingResult extends AbstractEntity {
	private static final long serialVersionUID = -4165099767077210852L;
	public static final int TYPE_PERFORMANCE = 0;
	public static final int TYPE_STABILITY = 1;
	public static final int TYPE_IDLE_TIMEOUT = 2;
	private String id;
	private String monitorId;
	private String taskId;
	private long collectedTime;
	private long startTime;
	private long endTime;
	/**
	 * 0为可用性，响应时间，性能数据 1为稳定性 2为空闲超时
	 */
	private int type;

	/**
	 * 错误码，没有错误，为0
	 */
	private int errno;
	private double avg;
	private double max;
	private double min;
	private double tr;
	private double av2;
	private double tr2;
	/**
	 * 错误信息，没有为空。
	 */
	private String errmsg;

}
