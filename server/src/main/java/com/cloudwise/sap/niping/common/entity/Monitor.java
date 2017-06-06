package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Monitor extends AbstractEntity {

	private static final long serialVersionUID = 6732242760258530600L;
	private String monitorId;
	private String name;
	private String country;
	private String province;
	private String city;
	private String isp;
	private String area;
	private String ip;
	private String nipingT;
	private int status;
	private String[] runningTaskIds;

}
