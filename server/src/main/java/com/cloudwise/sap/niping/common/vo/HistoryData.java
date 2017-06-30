package com.cloudwise.sap.niping.common.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HistoryData {

    List<Metrics> trMetrics;
    List<Metrics> avMetrics;

    AnalysisUsable performance;
    AnalysisUsable stability;
    AnalysisUsable idleTimeout;

}