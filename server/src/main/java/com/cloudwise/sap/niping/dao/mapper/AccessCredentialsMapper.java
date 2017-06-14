package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.entity.AccessCredentials;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessCredentialsMapper implements ResultSetMapper<AccessCredentials> {

    @Override
    public AccessCredentials map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        AccessCredentials accessCredentials = AccessCredentials.builder()
                .token(r.getString("TOKEN"))
                .comment(r.getString("COMMENT"))
                .expiryDate(r.getDate("EXPIRY_DATE")).build();
        accessCredentials.set(r.getString("ACCOUNT_ID"), r.getDate("CREATION_TIME"), r.getDate("MODIFIED_TIME"));
        return accessCredentials;
    }

}