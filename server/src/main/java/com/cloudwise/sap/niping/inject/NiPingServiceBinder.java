package com.cloudwise.sap.niping.inject;

import com.cloudwise.sap.niping.SapConfiguration;
import com.cloudwise.sap.niping.auth.SapBasicAuthenticator;
import com.cloudwise.sap.niping.auth.SapOAuthenticator;
import com.cloudwise.sap.niping.dao.*;
import com.cloudwise.sap.niping.service.AuthService;
import com.cloudwise.sap.niping.service.MonitorService;
import com.cloudwise.sap.niping.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.skife.jdbi.v2.DBI;

import javax.inject.Singleton;

@Data
@AllArgsConstructor
public class NiPingServiceBinder extends AbstractBinder {

    private DBI jdbi;
    private ObjectMapper jsonMapper;
    private SapConfiguration sapConfiguration;

    @Override
    protected void configure() {
        bind(jsonMapper).to(ObjectMapper.class);
        //dao
        bind(jdbi.onDemand(AccessCredentialsDao.class)).to(AccessCredentialsDao.class);
        bind(jdbi.onDemand(UserDao.class)).to(UserDao.class);
        bind(jdbi.onDemand(TaskDao.class)).to(TaskDao.class);
        bind(jdbi.onDemand(MonitorDao.class)).to(MonitorDao.class);
        bind(jdbi.onDemand(MonitorTaskDao.class)).to(MonitorTaskDao.class);
        bind(jdbi.onDemand(MonitorNiPingResultDao.class)).to(MonitorNiPingResultDao.class);

        //config
        bind(sapConfiguration).to(SapConfiguration.class);
        //jdbi
        bind(jdbi).to(DBI.class);
        //service
        bind(AuthService.class).in(Singleton.class).to(AuthService.class);
        bind(MonitorService.class).in(Singleton.class).to(MonitorService.class);
        bind(TaskService.class).in(Singleton.class).to(TaskService.class);
        //auth
        bind(SapOAuthenticator.class).in(Singleton.class).to(SapOAuthenticator.class);
        bind(SapBasicAuthenticator.class).in(Singleton.class).to(SapBasicAuthenticator.class);
    }
}
