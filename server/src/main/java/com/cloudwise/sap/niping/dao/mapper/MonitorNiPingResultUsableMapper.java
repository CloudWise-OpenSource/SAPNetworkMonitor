package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonitorNiPingResultUsableMapper implements ResultSetMapper<MonitorNiPingResult> {

    @Override
    public MonitorNiPingResult map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return MonitorNiPingResult.builder()
                .type(r.getInt("TYPE"))
                .isUsable(r.getInt("ERRNO") == 0 ? true : false)
                .build();
    }
}