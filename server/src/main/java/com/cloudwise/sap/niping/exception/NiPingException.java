package com.cloudwise.sap.niping.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NiPingException extends RuntimeException{
    String code ;
    private String errorMessage;
}