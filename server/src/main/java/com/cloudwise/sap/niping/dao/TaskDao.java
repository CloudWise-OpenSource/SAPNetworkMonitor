package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.dao.mapper.TaskMapper;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.MaxRows;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.Date;
import java.util.List;

@Service
@UseStringTemplate3StatementLocator
public interface TaskDao {

    @SqlUpdate("INSERT INTO SNM_TASK (TASK_ID, ACCOUNT_ID, NAME, TASK_INTERVAL, CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME)" +
            " VALUES (:taskId, :accountId, :name, :interval, :configJson, :status, :creationTime, :modifiedTime)")
    void insertTask(@BindBean Task task);

    @SqlUpdate("UPDATE SNM_TASK SET NAME = :name, TASK_INTERVAL = :interval, MODIFIED_TIME = :modifiedTime WHERE TASK_ID = :taskId")
    void updateTask(@BindBean Task task) ;

    @SqlUpdate("UPDATE SNM_TASK SET STATUS = :status, MODIFIED_TIME = :modifiedTime WHERE TASK_ID = :taskId")
    void updateTaskStatus(@Bind("taskId") String taskId, @Bind("status") int status, @Bind("modifiedTime") Date modifiedTime) ;

    @SqlQuery("SELECT SNM_TASK.TASK_ID, MONITOR_ID, ACCOUNT_ID, NAME, TASK_INTERVAL AS 'INTERVAL', CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME FROM SNM_TASK INNER JOIN SNM_MONITOR_TASK ON SNM_MONITOR_TASK.TASK_ID = SNM_TASK.TASK_ID " +
            " WHERE SNM_MONITOR_TASK.MONITOR_ID = :monitorId AND SNM_MONITOR_TASK.TASK_ID NOT IN (<runningTaskIds>) AND STATUS = :taskEnableStatus")
    @MaxRows(1)
    @RegisterMapper(TaskMapper.class)
    Task getNextStartTask(@Bind("monitorId") String monitorId, @BindIn("runningTaskIds") List<String> runningTaskIds, @Bind("taskEnableStatus") int taskEnableStatus) ;

    @SqlQuery("SELECT SNM_TASK.TASK_ID, MONITOR_ID, ACCOUNT_ID, NAME, TASK_INTERVAL AS 'INTERVAL', CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME FROM SNM_TASK INNER JOIN SNM_MONITOR_TASK ON SNM_MONITOR_TASK.TASK_ID = SNM_TASK.TASK_ID " +
            " WHERE SNM_MONITOR_TASK.MONITOR_ID = :monitorId AND REDISPATCHER = :needRedispatcher AND STATUS = :taskEnableStatus")
    @MaxRows(1)
    @RegisterMapper(TaskMapper.class)
    Task getNextRestartTask(@Bind("monitorId") String monitorId, @Bind("needRedispatcher") int needRedispatcher, @Bind("taskEnableStatus") int taskEnableStatus) ;

    @SqlQuery("SELECT SNM_TASK.TASK_ID AS taskId FROM SNM_TASK INNER JOIN SNM_MONITOR_TASK ON SNM_MONITOR_TASK.TASK_ID = SNM_TASK.TASK_ID " +
            " WHERE SNM_MONITOR_TASK.MONITOR_ID = :monitorId AND SNM_MONITOR_TASK.TASK_ID IN (<runningTaskIds>) AND STATUS = :taskEnableStatus")
    List<String> getAllTaskIdsInRunningTaskIds(@Bind("monitorId") String monitorId, @BindIn("runningTaskIds") List<String> runningTaskIds, @Bind("taskEnableStatus") int taskEnableStatus) ;

    @SqlQuery("SELECT SNM_TASK.TASK_ID, MONITOR_ID, ACCOUNT_ID, NAME, TASK_INTERVAL AS 'INTERVAL', CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME FROM SNM_TASK LEFT JOIN SNM_MONITOR_TASK ON SNM_MONITOR_TASK.TASK_ID = SNM_TASK.TASK_ID " +
            " WHERE ACCOUNT_ID = :accountId AND STATUS = :taskEnableStatus ORDER BY TASK_ID")
    @RegisterMapper(TaskMapper.class)
    List<Task> selectByAccountIds(@Bind("accountId") String accountId, @Bind("taskEnableStatus") int taskEnableStatus);
}
