package com.cloudwise.sap.niping.common.vo;

import com.cloudwise.sap.niping.common.entity.JobDesc;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class Task {

    private String taskId;
    private String name;
    private int interval;
    private String monitorIds;
    private String monitorNames;
    private JobDesc jobDesc;

    private String selectMonitorIdString;

    private int status;
    private boolean isAvailable;

    public enum Status {
        available(1), partavailable(2), allnotavailable(3), disable(0);

        @Getter
        private int status;

        Status(int status) {this.status = status;}
    }

    public Task() {}

    public Task(String taskId, String name, int interval, String monitorIds, String monitorNames, JobDesc jobDesc, String
            selectMonitorIdString, int status, boolean isAvailable) {
        this.taskId = taskId;
        this.name = name;
        this.interval = interval;
        this.monitorIds = monitorIds;
        this.monitorNames = monitorNames;
        this.jobDesc = jobDesc;
        this.selectMonitorIdString = selectMonitorIdString;
        this.status = status;
        this.isAvailable = isAvailable;
    }
}