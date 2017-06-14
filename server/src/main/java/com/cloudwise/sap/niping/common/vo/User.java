package com.cloudwise.sap.niping.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class User {
	protected String userId;
	protected String accountId;
	protected String name;
	protected String loginName;
	private String password;
	private String passwordSalt;
}
