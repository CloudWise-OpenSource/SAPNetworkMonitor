package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Task extends AbstractEntity {

	private static final long serialVersionUID = 5824620618374870987L;
	public static final int STATUS_ENABLED = 0;
	public static final int STATUS_DISABLED = 1;
	public static final int STATUS_DELETED = 2;

	private String taskId;
	private String name;
	private int interval;
	private String configJson;
	private int status;
}
