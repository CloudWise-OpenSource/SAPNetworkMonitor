package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.vo.Metrics;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class MetricsMapper implements ResultSetMapper<Metrics> {

    @Override
    public Metrics map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        return Metrics.builder()
                .time(new Date(r.getLong("COLLECTED_TIME")))
                .value(r.getDouble("VALUE"))
                .build();
    }
}
