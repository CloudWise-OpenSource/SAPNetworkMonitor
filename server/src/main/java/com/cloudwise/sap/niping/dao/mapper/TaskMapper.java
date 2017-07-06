package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class TaskMapper implements ResultSetMapper<Task> {

    @Override
    public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Task task = Task.builder()
                .taskId(r.getString("TASK_ID"))
                .name(r.getString("NAME"))
                .interval(r.getInt("INTERVAL"))
                .configJson(r.getString("CONFIG_JSON"))
                .status(r.getInt("STATUS")).build();

        try {
            task.setMonitorId(r.getString("MONITOR_ID"));
            task.setMonitorName(r.getString("MONITOR_NAME"));
        }
        catch (Exception e) {
            log.debug(ExceptionUtils.getMessage(e));
        }

        try {
            task.setResultId(r.getString("RESULT_ID"));
            task.setErrno(r.getInt("ERRNO"));
        }
        catch (Exception e) {
            log.debug(ExceptionUtils.getMessage(e));
        }
        task.set(r.getString("ACCOUNT_ID"), r.getDate("CREATION_TIME"), r.getDate("MODIFIED_TIME"));
        return task;
    }
}