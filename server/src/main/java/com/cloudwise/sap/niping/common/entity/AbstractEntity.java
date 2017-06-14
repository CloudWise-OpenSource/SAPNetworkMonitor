package com.cloudwise.sap.niping.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 6918525402366846237L;

	private String accountId;
	private Date creationTime;
	private Date modifiedTime;

	public void set(String accountId, Date creationTime, Date modifiedTime) {
		this.accountId = accountId;
		this.creationTime = creationTime;
		this.modifiedTime = modifiedTime;
	}
}