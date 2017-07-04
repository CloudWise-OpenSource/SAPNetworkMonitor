package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.MonitorNiPingResult;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitorNiPingResultListMapper implements ResultSetMapper<MonitorNiPingResult> {

    @Override
    public MonitorNiPingResult map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String collectedTimeString = null;
        try {
            collectedTimeString = dateFormat.format(new Date(r.getLong("COLLECTED_TIME")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String startTimeString = null;
        try {
            startTimeString = dateFormat.format(new Date(r.getLong("START_TIME")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String endTimeString = null;
        try {
            endTimeString = dateFormat.format(new Date(r.getLong("END_TIME")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int type = 0;
        String typeString = null;
        try {
            type = r.getInt("TYPE");
            if (type == MonitorNiPingResult.Type.PERFORMANCE.getValue()) {typeString = "时延监测";}
            else if (type == MonitorNiPingResult.Type.STABILITY.getValue()) {typeString = "稳定性监测";}
            else if (type == MonitorNiPingResult.Type.IDLE_TIMEOUT.getValue()) {typeString = "超时监测";}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            collectedTimeString = dateFormat.format(new Date(r.getLong("COLLECTED_TIME")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean isUsable = false;
        int errno = 0;
        try {
            errno = r.getInt("ERRNO");
            if (errno == 0) {
                isUsable = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String errMsg = null;
        try {
            errMsg = r.getString("ERRMSG");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        DecimalFormat formatter = new DecimalFormat("#0.000");
        String av2 = null;
        String tr2 = null;
        try {
            av2 = formatter.format(r.getDouble("AV2"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            tr2 = formatter.format(r.getDouble("TR2"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return MonitorNiPingResult.builder()
                .collectedTimeString(collectedTimeString)
                .startTimeString(startTimeString)
                .endTimeString(endTimeString)
                .type(type)
                .typeString(typeString)
                .av2String(av2)
                .isUsable(isUsable)
                .errno(errno)
                .errmsg(errMsg)
                .tr2String(tr2)
                .build();
    }
}