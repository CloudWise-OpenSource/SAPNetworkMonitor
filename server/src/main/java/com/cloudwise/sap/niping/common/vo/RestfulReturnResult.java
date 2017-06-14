package com.cloudwise.sap.niping.common.vo;

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
}
