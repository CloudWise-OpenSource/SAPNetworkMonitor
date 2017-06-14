package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements ResultSetMapper<Task> {

    @Override
    public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        Task task = Task.builder()
        .taskId(r.getString("TASK_ID"))
        .monitorId(r.getString("MONITOR_ID"))
        .name(r.getString("NAME"))
        .interval(r.getInt("INTERVAL"))
        .configJson(r.getString("CONFIG_JSON"))
        .status(r.getInt("STATUS")).build();
        task.set(r.getString("ACCOUNT_ID"), r.getDate("CREATION_TIME"), r.getDate("MODIFIED_TIME"));
        return task;
    }
}