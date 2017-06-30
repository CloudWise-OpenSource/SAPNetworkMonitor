package com.cloudwise.sap.niping;

import lombok.Data;

@Data
public class JobConfiguration {

    private int roundTripTimeB;
    private int roundTripTimeL;
    // 带宽
    private int bandwidthB;
    private int bandwidthL;
    // 稳定性
    private int stabilityB;
    private int stabilityL;
    private int stabilityD;
    // 闲置超时
    private int idleTimeoutD;
}
