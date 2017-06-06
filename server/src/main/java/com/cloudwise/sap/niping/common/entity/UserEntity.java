package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserEntity extends AbstractEntity {

	private static final long serialVersionUID = -4131084870810239557L;
	private String userId;
	private String loginName;
	private String name;
	private String password;
	private String passwordSalt;
	private int status;

}
