package com.cloudwise.sap.niping.common.vo;

import com.cloudwise.sap.niping.common.entity.JobDesc;
import lombok.Builder;
import lombok.Data;
import org.jvnet.hk2.annotations.Service;

@Data
@Builder
@Service
public class Task {

    private String taskId;
    private String name;
    private int interval;
    private String monitorIds;
    private JobDesc jobDesc;

}