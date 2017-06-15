package com.cloudwise.sap.niping.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class NiPingException extends Exception{
    private int code;
    private String errorMessage;

    public NiPingException(Exception e) {
        this.code = e.getCode();
        this.errorMessage = e.getErrorMessage();
    }

    public enum Exception {
        DBError(2001, "database access error."),
        ServerError(2002, "server error.");

        @Getter
        private int code;
        @Getter
        private String errorMessage;

        Exception(int code, String errorMessage) {
            this.code = code;
            this.errorMessage = errorMessage;
        }
    }
}