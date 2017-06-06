package com.cloudwise.sap.niping.auth;

import java.util.Optional;

import com.cloudwise.sap.niping.common.vo.User;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {
	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
		if ("yunzhihui123".equals(credentials.getPassword())) {
			return Optional.of(new User("1", credentials.getUsername()));
		}
		return Optional.empty();
	}
}