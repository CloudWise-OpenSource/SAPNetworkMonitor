package com.cloudwise.sap.niping.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends AbstractEntity {

	private static final long serialVersionUID = 5824620618374870988L;

	private String taskId;
	private String name;
	private int interval;
	private String configJson;
	private int status;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@JsonIgnore
	private MonitorJob.Action action;
	@JsonIgnore
	private String monitorId;

	public enum Status {
		enable(1), disable(0), deleted(-1);

		@Getter
		private int status;
		Status(int status){this.status = status;}
	}
}
