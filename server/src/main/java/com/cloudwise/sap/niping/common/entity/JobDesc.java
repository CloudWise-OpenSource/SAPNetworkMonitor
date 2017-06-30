package com.cloudwise.sap.niping.common.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobDesc {
	private String router;
	// 时延
	@Builder.Default
	private int roundTripTimeB = 1;
	@Builder.Default
	private int roundTripTimeL = 100;
	// 带宽
	@Builder.Default
	private int bandwidthB = 100000;
	@Builder.Default
	private int bandwidthL = 10;
	// 稳定性
	@Builder.Default
	private int stabilityB = 200;
	@Builder.Default
	private int stabilityL = 36000;
	@Builder.Default
	private int stabilityD = 1000;
	// 闲置超时
	@Builder.Default
	private int idleTimeoutD = 3600000;
}
