package com.cloudwise.sap.niping.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode()
public class MonitorTask {
    private String monitorId;
    private String taskId;
    private int redispatcher;

    public enum Redispatcher {
        NeedRedispatcher(1), NoNeedRedispatcher(0);

        @Getter
        private int redispatcher;
        Redispatcher(int redispatcher){this.redispatcher = redispatcher;}
    }
}