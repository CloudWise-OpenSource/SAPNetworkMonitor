package com.cloudwise.sap.niping.common.constant;

import lombok.Getter;

public enum Result {

	SUCCESS(1000, "success"),

	Error(2000, "error"),
	DBError(2001, "database access error."),
	ServerError(2002, "server error.");

	@Getter
	private int code;
	@Getter
	private String message;

	Result(int code, String message) {
		this.code = code;
		this.message = message;
	}
}