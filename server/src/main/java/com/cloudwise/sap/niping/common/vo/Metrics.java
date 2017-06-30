package com.cloudwise.sap.niping.common.vo;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Metrics {

    private double value;
    private Date time;

}