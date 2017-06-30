package com.cloudwise.sap.niping.dao.mapper;


import com.cloudwise.sap.niping.common.entity.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultTaskMapper implements ResultSetMapper<Task> {

    @Override
    public Task map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return Task.builder()
                .taskId(r.getString("TASK_ID"))
                .name(r.getString("NAME"))
                .build();
    }
}