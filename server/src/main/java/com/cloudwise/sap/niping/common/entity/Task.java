package com.cloudwise.sap.niping.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task extends AbstractEntity {

    private static final long serialVersionUID = 5824620618374870988L;

    private String taskId;
    private String name;
    private int interval;
    private String configJson;
    private int status;

    @JsonIgnore
    private JobDesc jobDesc;
    @JsonIgnore
    private List<Monitor> monitors;
    @JsonIgnore
    private List<String> selectMonitorIds;
    @JsonIgnore
    private String selectMonitorIdString;
    @JsonIgnore
    private MonitorJob.Action action;
    @JsonIgnore
    private String monitorId;
    @JsonIgnore
    private String monitorName;
    @JsonIgnore
    private String resultMonitorId;
    @JsonIgnore
    private int errno;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Status {
        enable(1), disable(0), deleted(-1);

        @Getter
        private int status;

        Status(int status) {this.status = status;}
    }
}