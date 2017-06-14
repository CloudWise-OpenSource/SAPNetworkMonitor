package com.cloudwise.sap.niping.auth;

import com.cloudwise.sap.niping.common.entity.AccessCredentials;
import com.cloudwise.sap.niping.service.AuthService;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Optional;

@Data
@Service
@Slf4j
public class SapOAuthenticator implements Authenticator<String, OAuthUser> {

    @Inject
    AuthService authService;

    @Override
    public Optional<OAuthUser> authenticate(String credentials) throws AuthenticationException {
        Optional<AccessCredentials> accessCredentials = authService.getCredentialsByToken(credentials);
        if (accessCredentials.isPresent()) {
            return Optional.of(OAuthUser.builder().accountId(accessCredentials.get().getAccountId()).build());
        }
        return Optional.empty();
    }
}
