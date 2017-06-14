package com.cloudwise.sap.niping.dao;

import com.cloudwise.sap.niping.common.entity.AccessCredentials;
import com.cloudwise.sap.niping.dao.mapper.AccessCredentialsMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

public interface AccessCredentialsDao {

    @SqlQuery("SELECT TOKEN, ACCOUNT_ID, COMMENT, EXPIRY_DATE, CREATION_TIME, MODIFIED_TIME FROM SNM_ACCESS_CREDENTIALS " +
            " WHERE TOKEN = :token")
    @RegisterMapper(AccessCredentialsMapper.class)
    public AccessCredentials getCredentialsByToken(@Bind("token") String token);
}