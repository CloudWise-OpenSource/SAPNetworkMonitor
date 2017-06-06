package com.cloudwise.sap.niping.common.entity;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

@Data
public abstract class AbstractEntity implements Serializable {
	private static final long serialVersionUID = 6918525402366846237L;
	/*
	 * @CreatedDate
	 */
	private String accountId;
	private Date creationTime;
	private Date modifiedTime;
}
