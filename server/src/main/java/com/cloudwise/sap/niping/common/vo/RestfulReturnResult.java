package com.cloudwise.sap.niping.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestfulReturnResult {

	private int code;
	private String msg;
	private Object data;
}
