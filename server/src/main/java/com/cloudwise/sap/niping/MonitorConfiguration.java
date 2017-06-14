package com.cloudwise.sap.niping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MonitorConfiguration {
    @JsonProperty
    private int lostTime;
}