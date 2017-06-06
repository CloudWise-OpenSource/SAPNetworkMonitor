package com.cloudwise.sap.niping.common.entity;

import java.sql.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccessCredentials extends AbstractEntity {
	private static final long serialVersionUID = 8305605853259461011L;
	private String token;
	private String comment;
	private Date expiryDate;
}
