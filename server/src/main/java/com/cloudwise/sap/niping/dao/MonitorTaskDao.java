package com.cloudwise.sap.niping.dao;

import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.BatchChunkSize;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@Service
@UseStringTemplate3StatementLocator
public interface MonitorTaskDao {
    // monitorTask
    @SqlBatch("INSERT INTO SNM_MONITOR_TASK(MONITOR_ID, TASK_ID) VALUES (:monitorId, :taskId)")
    @BatchChunkSize(1000)
    void insertMonitorTask(@Bind("monitorId")List<String> monitorIds, @Bind("taskId") String taskId);

    @SqlUpdate("UPDATE SNM_MONITOR_TASK SET REDISPATCHER = :needRedispatcher WHERE TASK_ID = :taskId")
    void updateMonitorTaskRedispatcher(@Bind("taskId") String taskId, @Bind("needRedispatcher") int needRedispatcher);

    @SqlUpdate("UPDATE SNM_MONITOR_TASK SET REDISPATCHER = :redispatchered WHERE TASK_ID = :taskId AND MONITOR_ID = :monitorId")
    void updateMonitorTaskRedispatcher(@Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("redispatchered") int redispatchered);

    @SqlUpdate("REMOVE SNM_MONITOR_TASK WHERE TASK_ID = :taskId")
    void removeMonitorTask(@Bind("taskId") String taskId);
}