package com.cloudwise.sap.niping;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.cloudwise.sap.niping.auth.ExampleAuthenticator;
import com.cloudwise.sap.niping.common.vo.User;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class NiPingMonitorApplication extends Application<ServerConfiguration> {

	public static void main(String[] args) throws Exception {
		new NiPingMonitorApplication().run(args);
	}

	@Override
	public void run(ServerConfiguration configuration, Environment environment) throws Exception {
		environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
				.setAuthenticator(new ExampleAuthenticator()).buildAuthFilter()));
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		// If you want to use @Auth to inject a custom Principal type into your
		// resource
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
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
