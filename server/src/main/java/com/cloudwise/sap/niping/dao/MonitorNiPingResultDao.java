package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import com.cloudwise.sap.niping.common.entity.Task;
import com.cloudwise.sap.niping.common.vo.Metrics;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.dao.mapper.*;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.List;

@Service
@UseStringTemplate3StatementLocator
public interface MonitorNiPingResultDao {

    @SqlUpdate("INSERT INTO SNM_NIPING_RESULT (ACCOUNT_ID, MONITOR_ID, TASK_ID, COLLECTED_TIME, TYPE, START_TIME, END_TIME, ERRNO, AVG, " +
            "MAX, MIN, TR, AV2, TR2, ERRMSG, CREATION_TIME, MODIFIED_TIME) " +
            " VALUES (:accountId, :monitorId, :taskId, :collectedTime, :type, :startTime, :endTime, :errno, :avg, :max, :min, :tr, :av2, " +
            ":tr2, :errmsg, :creationTime, :modifiedTime)")
    void saveMonitorNiPingResult(@BindBean MonitorNiPingResult monitorNiPingResult);

    @SqlQuery("SELECT TMP.*, M.MONITOR_ID, M.NAME, M.IP, M.COUNTRY, M.PROVINCE, M.CITY, M.ISP FROM " +
            "(SELECT TASK_ID, AV2, TR2, ERRNO, MONITOR_ID FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.TASK_ID = :taskId AND TYPE = :type AND NOT EXISTS(SELECT 1 FROM SNM_NIPING_RESULT WHERE TASK_ID = R" +
            ".TASK_ID AND MONITOR_ID = R.MONITOR_ID AND COLLECTED_TIME <g> R.COLLECTED_TIME AND  ACCOUNT_ID = :accountId AND TASK_ID = :taskId AND TYPE = :type)) AS TMP " +
            "INNER JOIN SNM_MONITOR M ON M.MONITOR_ID = TMP.MONITOR_ID")
    @RegisterMapper(MonitorNiPingResultMapper.class)
    List<MonitorNiPingResult> selectByTaskId(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("type") int type, @Define("g") String g);

    @SqlQuery("SELECT TASK_ID, ERRNO, ERRNO, TR2, AV2, M.MONITOR_ID, M.NAME, M.IP, M.COUNTRY, M.PROVINCE, M.CITY, M.ISP FROM SNM_NIPING_RESULT R " +
            "INNER JOIN SNM_MONITOR M ON M.MONITOR_ID = R.MONITOR_ID " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.TASK_ID = :taskId AND TYPE = :type AND collected_Time <ge> :startDate")
    @RegisterMapper(MonitorNiPingResultMapper.class)
    List<MonitorNiPingResult> selectByTaskId(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("startDate") long startTime, @Bind("type") int type, @Define("ge") String ge);

    @SqlQuery("SELECT R.TASK_ID, NAME FROM SNM_NIPING_RESULT R INNER JOIN SNM_TASK T ON T.TASK_ID = R.TASK_ID WHERE T.ACCOUNT_ID = " +
            ":accountId GROUP BY R.TASK_ID")
    @RegisterMapper(ResultTaskMapper.class)
    List<Task> selectTasks(@Bind("accountId") String accountId);

    @SqlQuery("SELECT M.MONITOR_ID, M.NAME, M.IP FROM (SELECT MONITOR_ID FROM SNM_NIPING_RESULT WHERE ACCOUNT_ID = :accountId AND TASK_ID = :taskId GROUP BY MONITOR_ID) TMP " +
            " INNER JOIN SNM_MONITOR M ON M.MONITOR_ID = TMP.MONITOR_ID <condition>")
    @RegisterMapper(ResultMonitorMapper.class)
    List<Monitor> selectMonitors(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Define("condition") String condition);

    @SqlQuery("SELECT COUNTRY FROM SNM_MONITOR M INNER JOIN SNM_NIPING_RESULT R ON R.MONITOR_ID = M.MONITOR_ID WHERE R.ACCOUNT_ID = :accountId GROUP BY COUNTRY")
    List<String> selectCountries(@Bind("accountId") String accountId);

    @SqlQuery("SELECT PROVINCE FROM SNM_MONITOR M INNER JOIN SNM_NIPING_RESULT R ON R.MONITOR_ID = M.MONITOR_ID WHERE R.ACCOUNT_ID = :accountId AND COUNTRY = :country GROUP BY PROVINCE")
    List<String> selectProvinces(@Bind("accountId") String accountId, @Bind("country") String country);

    @SqlQuery("SELECT CITY FROM SNM_MONITOR M INNER JOIN SNM_NIPING_RESULT R ON R.MONITOR_ID = M.MONITOR_ID WHERE R.ACCOUNT_ID = :accountId AND COUNTRY = :country AND PROVINCE = :province  GROUP BY CITY")
    List<String> selectCities(@Bind("accountId") String accountId, @Bind("country") String country, @Bind("province") String province);

    @SqlQuery("SELECT R.COLLECTED_TIME, R.TR2 AS VALUE " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND TYPE = :type AND R.COLLECTED_TIME <ge> :time AND R.TR2 IS NOT NULL ORDER BY R.COLLECTED_TIME")
    @RegisterMapper(MetricsMapper.class)
    List<Metrics> selectTRMetrics(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("type") int type, @Bind("time") long time, @Define("ge") String ge);

    @SqlQuery("SELECT R.COLLECTED_TIME, R.AV2 AS VALUE " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND TYPE = :type AND R.COLLECTED_TIME <ge> :time AND R.AV2 IS NOT NULL <condition> ORDER BY R.COLLECTED_TIME")
    @RegisterMapper(MetricsMapper.class)
    List<Metrics> selectAVMetrics(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("type") int type, @Bind("time") long time, @Define("ge") String ge);

    @SqlQuery("SELECT R.TYPE, R.ERRNO " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND R.COLLECTED_TIME <ge> :time <condition> ")
    @RegisterMapper(MonitorNiPingResultUsableMapper.class)
    List<MonitorNiPingResult> getMonitorNiPingResult(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("time") long time, @Define("ge") String ge);

    @SqlQuery("SELECT R.COLLECTED_TIME, R.START_TIME, R.END_TIME, R.TYPE, R.AV2, R.ERRNO, R.ERRMSG, R.TR2 " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND R.COLLECTED_TIME <ge> :time <condition> ORDER BY R.COLLECTED_TIME DESC <page>")
    @RegisterMapper(MonitorNiPingResultListMapper.class)
    List<MonitorNiPingResult> select(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("time") long time, @Define("condition") String condition, @Define("page") String page, @Define("ge") String ge);

    @SqlQuery("SELECT R.COLLECTED_TIME, R.START_TIME, R.END_TIME, R.TYPE, R.AV2, R.ERRNO, R.ERRMSG, R.TR2 " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND R.COLLECTED_TIME <ge> :time <condition> ORDER BY R.COLLECTED_TIME DESC")
    @RegisterMapper(MonitorNiPingResultListMapper.class)
    List<MonitorNiPingResult> selectAll(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("time") long time, @Define("condition") String condition, @Define("ge") String ge);

    @SqlQuery("SELECT COUNT(1) " +
            "FROM SNM_NIPING_RESULT R " +
            "WHERE R.ACCOUNT_ID = :accountId AND R.MONITOR_ID = :monitorId AND R.TASK_ID = :taskId AND R.COLLECTED_TIME <ge> :time <condition> ")
    long count(@Bind("accountId") String accountId, @Bind("taskId") String taskId, @Bind("monitorId") String monitorId, @Bind("time") long time, @Define("condition") String condition, @Define("ge") String ge);
}
