package com.cloudwise.sap.niping;

import com.cloudwise.sap.niping.auth.*;
import com.cloudwise.sap.niping.inject.NiPingServiceBinder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.PolymorphicAuthDynamicFeature;
import io.dropwizard.auth.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.skife.jdbi.v2.DBI;

public class NiPingMonitorApplication extends Application<ServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new NiPingMonitorApplication().run(args);
    }

    @Override
    public void run(ServerConfiguration configuration, Environment environment) throws Exception {

        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "sapData");

        ObjectMapper objectMapper = environment.getObjectMapper();
        SapConfiguration sapConfiguration = configuration.getSapConfig();
        NiPingServiceBinder niPingServiceBinder = new NiPingServiceBinder(jdbi, objectMapper, sapConfiguration);

        ServiceLocator serviceLocator = ServiceLocatorUtilities.bind(niPingServiceBinder);
        SapBasicAuthenticator sapBasicAuthenticator = ServiceLocatorUtilities.getService(serviceLocator, SapBasicAuthenticator.class.getName());
        SapOAuthenticator sapOAuthenticator = ServiceLocatorUtilities.getService(serviceLocator, SapOAuthenticator.class.getName());

        final BasicCredentialAuthFilter basicAuthFilter = new BasicCredentialAuthFilter.Builder<BasicAuthUser>()
                .setAuthenticator(sapBasicAuthenticator)
                .buildAuthFilter();
        final AuthFilter oAuthFilter = new OAuthCredentialAuthFilter.Builder<OAuthUser>()
                .setAuthenticator(sapOAuthenticator)
                .setPrefix("Bearer")
                .buildAuthFilter();

        final PolymorphicAuthDynamicFeature feature = new PolymorphicAuthDynamicFeature<UserPrincipal>(ImmutableMap.of(BasicAuthUser.class, basicAuthFilter, OAuthUser.class, oAuthFilter));
        final AbstractBinder binder = new PolymorphicAuthValueFactoryProvider.Binder<>(
                ImmutableSet.of(BasicAuthUser.class, OAuthUser.class));

        environment.jersey().register(feature);
        environment.jersey().register(binder);

        environment.jersey().register(niPingServiceBinder);
        environment.jersey().packages("com.cloudwise.sap.niping");
    }

    @Override
    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<ServerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ServerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }
}
