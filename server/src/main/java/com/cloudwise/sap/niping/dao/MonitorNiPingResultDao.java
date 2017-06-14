package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

@Service
@UseStringTemplate3StatementLocator
public interface MonitorNiPingResultDao {

    @SqlUpdate("INSERT INTO SNM_NIPING_RESULT (ACCOUNT_ID, MONITOR_ID, TASK_ID, COLLECTED_TIME, TYPE, START_TIME, END_TIME, ERRNO, AVG, MAX, MIN, TR, AV2, TR2, ERRMSG, CREATION_TIME, MODIFIED_TIME) " +
            " VALUES (:accountId, :monitorId, :taskId, :collectedTime, :type, :startTime, :endTime, :errno, :avg, :max, :min, :tr, :av2, :tr2, :errmsg, :creationTime, :modifiedTime)")
    void saveMonitorNiPingResult(@BindBean MonitorNiPingResult monitorNiPingResult);
}
