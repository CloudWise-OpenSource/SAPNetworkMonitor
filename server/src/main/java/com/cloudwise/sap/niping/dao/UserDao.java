package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.vo.User;
import com.cloudwise.sap.niping.dao.mapper.UserMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

public interface UserDao {

    @SqlQuery("SELECT USER_ID, ACCOUNT_ID, NAME, PASSWORD, PASSWORD_SALT, LOGIN_NAME FROM SNM_USER " +
            " WHERE LOGIN_NAME = :loginName")
    @RegisterMapper(UserMapper.class)
    public User getUser(@Bind("loginName") String loginName);

}