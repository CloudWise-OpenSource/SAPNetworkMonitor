package com.cloudwise.sap.niping.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalysisUsable {

    private long usable;
    private long notUsable;

}