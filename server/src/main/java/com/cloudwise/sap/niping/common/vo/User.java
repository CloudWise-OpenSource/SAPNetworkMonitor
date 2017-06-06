package com.cloudwise.sap.niping.common.vo;

import java.security.Principal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User implements Principal {
	private String accountId;
	private String name;
}
