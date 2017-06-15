package com.cloudwise.sap.niping.exception;

import com.cloudwise.sap.niping.common.constant.Result;
import lombok.Data;

@Data
public class NiPingException extends Exception {
    private int code;
    private String errorMessage;

    public NiPingException(Result errorResult) {
        this.code = errorResult.getCode();
        this.errorMessage = errorResult.getMessage();
    }
}