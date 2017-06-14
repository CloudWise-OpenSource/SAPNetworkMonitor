package com.cloudwise.sap.niping.common.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobDesc {
	private String router;
	// 时延
	private int roundTripTimeB = 1;
	private int roundTripTimeL = 100;
	// 带宽
	private int bandwidthB = 100000;
	private int bandwidthL = 10;
	// 稳定性
	private int stabilityB = 200;
	private int stabilityL = 36000;
	private int stabilityD = 1000;
	// 闲置超时
	private int idleTimeoutD = 3600000;
}
