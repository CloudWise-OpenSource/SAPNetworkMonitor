package com.cloudwise.sap.niping.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorNiPingResult extends AbstractEntity {
	private static final long serialVersionUID = -4165099767077210852L;
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


	public enum Type {

		PERFORMANCE(0), STABILITY(1), IDLE_TIMEOUT(2);

		@Getter
		private int value;

		Type(int value) {
			this.value = value;
		}
	}


	@JsonIgnore
	private Monitor monitor;

	@JsonIgnore
	private boolean isNoTime;

	@JsonIgnore
	private boolean isUsable;

	@JsonIgnore
	private String collectedTimeString;

	@JsonIgnore
	private String startTimeString;

	@JsonIgnore
	private String endTimeString;

	@JsonIgnore
	private String typeString;

	@JsonIgnore
	private String av2String;
	@JsonIgnore
	private String tr2String;
	@JsonIgnore
	private String usableString;

	public int getType() {
		return type;
	}
}