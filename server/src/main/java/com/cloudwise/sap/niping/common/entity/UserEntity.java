package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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

	public enum Status {
		enable(1), disable(0), deleted(-1);

		@Getter
		private int status;
		Status(int status){this.status = status;}
	}
}