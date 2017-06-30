package com.cloudwise.sap.niping.dao.mapper;


import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.Task;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultMonitorMapper implements ResultSetMapper<Monitor> {

    @Override
    public Monitor map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return Monitor.builder()
                .monitorId(r.getString("MONITOR_ID"))
                .name(r.getString("NAME"))
                .ip(r.getString("IP"))
                .build();
    }
}