package com.cloudwise.sap.niping.auth;

import com.cloudwise.sap.niping.common.vo.User;
import com.cloudwise.sap.niping.service.AuthService;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.inject.Inject;
import java.util.Optional;


public class SapBasicAuthenticator implements Authenticator<BasicCredentials, BasicAuthUser> {

    @Inject
    AuthService authService;

    public Optional<BasicAuthUser> authenticate(BasicCredentials credentials) throws AuthenticationException {
        Optional<User> optionalUser = authService.validateUser(credentials.getUsername(), credentials.getPassword());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return Optional.of(BasicAuthUser.builder()
                    .userId(user.getUserId())
                    .accountId(user.getAccountId())
                    .name(user.getName())
                    .loginName(user.getLoginName())
                    .build());
        }
        return Optional.empty();
    }
}