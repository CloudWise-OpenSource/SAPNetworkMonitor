package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.Monitor;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonitorMapper implements ResultSetMapper<Monitor> {

    @Override
    public Monitor map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        int status = r.getInt("STATUS");
        boolean isUsable = true;
        if (status == 0) {
            isUsable = false;
        }

        Monitor monitor = Monitor.builder()
                .monitorId(r.getString("MONITOR_ID"))
                .version(r.getString("VERSION"))
                .name(r.getString("NAME"))
                .country(r.getString("COUNTRY"))
                .province(r.getString("PROVINCE"))
                .city(r.getString("CITY"))
                .isp(r.getString("ISP"))
                .area(r.getString("AREA"))
                .ip(r.getString("IP"))
                .nipingT(r.getString("NIPING_T"))
                .status(status)
                .isUsable(isUsable).build();

        monitor.set(r.getString("ACCOUNT_ID"), r.getDate("CREATION_TIME"), r.getDate("MODIFIED_TIME"));
        return monitor;
    }
}