package com.cloudwise.sap.niping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SapConfiguration {
    @JsonProperty("monitor")
    private MonitorConfiguration monitorConfiguration;
}

