package com.cloudwise.sap.niping.dao.mapper;

import com.cloudwise.sap.niping.common.vo.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper  implements ResultSetMapper<User> {

    @Override
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return User.builder()
                .userId(r.getString("USER_ID"))
                .accountId(r.getString("ACCOUNT_ID"))
                .loginName(r.getString("LOGIN_NAME"))
                .name(r.getString("NAME"))
                .password(r.getString("PASSWORD"))
                .passwordSalt(r.getString("PASSWORD_SALT"))
                .build();
    }
}