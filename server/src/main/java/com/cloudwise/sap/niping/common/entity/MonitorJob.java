package com.cloudwise.sap.niping.common.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class MonitorJob {

	private String monitorId;
	private String taskId;
	private int interval;
	private int actionType;
	private JobDesc jobDesc;
	private long modifiedTime;

	public enum Action {

		START(1), STOP(0), RESTART(2);

		@Getter
		private int value;

		Action(int value) {
			this.value = value;
		}
	}
}
