package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.Monitor;
import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MonitorNiPingResultMapper implements ResultSetMapper<MonitorNiPingResult> {

    @Override
    public MonitorNiPingResult map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        Monitor monitor = Monitor.builder()
                .monitorId(r.getString("MONITOR_ID"))
                .name(r.getString("NAME"))
                .ip(r.getString("IP"))
                .country(r.getString("COUNTRY"))
                .province(r.getString("PROVINCE"))
                .city(r.getString("CITY"))
                .isp(r.getString("ISP"))
                .build();


        MonitorNiPingResult result = MonitorNiPingResult.builder()
                    .monitor(monitor)
                    .monitorId(r.getString("MONITOR_ID"))
                    .av2(r.getDouble("AV2"))
                    .tr2(r.getDouble("TR2"))
                    .errno(r.getInt("ERRNO"))
                    .isUsable(r.getInt("ERRNO") == 0 ? true:false)
                    .build();
            try {
                result.setTaskId(r.getString("TASK_ID"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return result;
    }
}