package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.dao.mapper.TaskMapper;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
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

    @SqlUpdate("UPDATE SNM_TASK SET NAME = :name, TASK_INTERVAL = :interval, CONFIG_JSON = :configJson, MODIFIED_TIME = :modifiedTime WHERE TASK_ID = :taskId AND ACCOUNT_ID = :accountId")
    void updateTask(@BindBean Task task) ;

    @SqlUpdate("UPDATE SNM_TASK SET STATUS = :status, MODIFIED_TIME = :modifiedTime WHERE TASK_ID = :taskId AND ACCOUNT_ID = :accountId AND STATUS <ne> :deleteStatus")
    void updateTaskStatus(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("status") int status,  @Bind("deleteStatus") int deleteStatus, @Bind("modifiedTime") Date modifiedTime, @Define("ne") String notEqual) ;

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
            " WHERE SNM_MONITOR_TASK.MONITOR_ID = :monitorId AND SNM_MONITOR_TASK.TASK_ID IN (<runningTaskIds>) AND SNM_TASK.STATUS = :taskEnableStatus")
    List<String> getAllTaskIdsInRunningTaskIds(@Bind("monitorId") String monitorId, @BindIn("runningTaskIds") List<String> runningTaskIds, @Bind("taskEnableStatus") int taskEnableStatus) ;

    @SqlQuery("SELECT *, RES.MONITOR_ID AS RESULT_MONITOR_ID, RES.ERRNO FROM (SELECT T.TASK_ID, MT.MONITOR_ID, M.NAME AS MONITOR_NAME, T.ACCOUNT_ID, T.NAME, TASK_INTERVAL AS 'INTERVAL', CONFIG_JSON, T.STATUS, T.CREATION_TIME, T.MODIFIED_TIME FROM SNM_TASK T " +
    "LEFT JOIN SNM_MONITOR_TASK MT ON MT.TASK_ID = T.TASK_ID " +
    "LEFT JOIN SNM_MONITOR M ON M.MONITOR_ID = MT.MONITOR_ID " +
    "WHERE T.ACCOUNT_ID = :accountId AND T.STATUS <ne> :taskDeleteStatus GROUP BY T.TASK_ID, MONITOR_ID) AS TMP " +
    "LEFT JOIN ( " +
     "SELECT  MAX(COLLECTED_TIME), MONITOR_ID, TASK_ID, ERRNO FROM SNM_NIPING_RESULT WHERE COLLECTED_TIME <ge> :lasthour GROUP BY TASK_ID, MONITOR_ID "+
    ") AS RES ON TMP.MONITOR_ID = RES.MONITOR_ID AND TMP.TASK_ID = RES.TASK_ID " +
    " ORDER BY TMP.CREATION_TIME DESC")
    @RegisterMapper(TaskMapper.class)
    List<Task> selectByAccountId(@Bind("accountId") String accountId, @Bind("taskDeleteStatus") int taskDeleteStatus, @Bind("lasthour") Date lasthour, @Define("ne") String notEqual, @Define("ge") String greaterThan);

    @SqlQuery("SELECT TASK_ID, ACCOUNT_ID, NAME, TASK_INTERVAL AS 'INTERVAL', " +
            " CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME FROM SNM_TASK " +
            "WHERE ACCOUNT_ID = :accountId")
    @RegisterMapper(TaskMapper.class)
    List<Task> selectByAccountId(@Bind("accountId") String accountId);

    @SqlQuery("SELECT * FROM (SELECT SNM_TASK.TASK_ID, MONITOR_ID, ACCOUNT_ID, NAME, TASK_INTERVAL AS 'INTERVAL', CONFIG_JSON, STATUS, CREATION_TIME, MODIFIED_TIME FROM SNM_TASK LEFT JOIN SNM_MONITOR_TASK ON SNM_MONITOR_TASK.TASK_ID = SNM_TASK.TASK_ID " +
            " WHERE ACCOUNT_ID = :accountId AND SNM_TASK.TASK_ID = :taskId AND SNM_TASK.STATUS <ne> :taskDeleteStatus) AS TMP GROUP BY MONITOR_ID")
    @RegisterMapper(TaskMapper.class)
    List<Task> get(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("taskDeleteStatus") int taskDeleteStatus, @Define("ne") String notEqual);

    @SqlQuery("SELECT T.TASK_ID, T.NAME FROM SNM_TASK T " +
            "INNER JOIN SNM_NIPING_RESULT R ON T.TASK_ID = R.TASK_ID " +
            "WHERE T.ACCOUNT_ID = :accountId GROUP BY TASK_ID")
    @RegisterMapper(TaskMapper.class)
    List<Task> selectTasksWithResult(@Bind("accountId") String accountId);
}
