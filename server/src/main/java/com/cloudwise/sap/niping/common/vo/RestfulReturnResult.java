package com.cloudwise.sap.niping.common.vo;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class RestfulReturnResult implements Serializable{

	private static final long serialVersionUID = 440244514382556045L;

	private int code;
	private String msg;
	private Object data;

	public RestfulReturnResult(Result result, Object data) {
		this.code = result.getCode();
		this.msg = result.getMessage();
		this.data = data;
	}

	public RestfulReturnResult(NiPingException exception, Object data) {
		this.code = exception.getCode();
		this.msg = exception.getErrorMessage();
		this.data = data;
	}
}