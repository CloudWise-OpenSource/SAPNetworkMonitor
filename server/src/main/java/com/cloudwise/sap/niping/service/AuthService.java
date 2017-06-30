package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.common.entity.AccessCredentials;
import com.cloudwise.sap.niping.common.entity.UserEntity;
import com.cloudwise.sap.niping.common.utils.HashStrategyUtil;
import com.cloudwise.sap.niping.common.vo.User;
import com.cloudwise.sap.niping.dao.AccessCredentialsDao;
import com.cloudwise.sap.niping.dao.UserDao;
import com.cloudwise.sap.niping.exception.NiPingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jvnet.hk2.annotations.Service;
import org.skife.jdbi.v2.exceptions.DBIException;

import javax.inject.Inject;
import java.util.Optional;

import static com.cloudwise.sap.niping.common.constant.Result.DBError;

@Slf4j
@Service
public class AuthService {

    @Inject
    private AccessCredentialsDao accessCredentialsDao;

    @Inject
    private UserDao userDao;

    public Optional<AccessCredentials> getCredentialsByToken(String credentials) {
        AccessCredentials accessCredentials = null;
        try {
            accessCredentials = accessCredentialsDao.getCredentialsByToken(credentials);
        } catch (DBIException e) {
            log.error("user auth: get credentials by token {} database error: {}", credentials, ExceptionUtils.getMessage(e));
        }
        return Optional.ofNullable(accessCredentials);
    }

    public Optional<User> validateUser(String loginName, String password) {
        User user = null;
        try {
            user = userDao.getUser(loginName, UserEntity.Status.enable.getStatus());
            if (null != user && user.getPassword().equals(HashStrategyUtil.computeHash(password, user.getPasswordSalt()))) {
                return Optional.of(user);
            }
        } catch (DBIException e) {
            log.error("user auth: get user {} database error: {}", loginName, ExceptionUtils.getMessage(e));
        } catch (Exception e) {
            log.error("user auth: validate user {} computeHash error: {}", loginName, ExceptionUtils.getMessage(e));
        }
        return Optional.empty();
    }

    public String getTokenByAccountId(String accountId) throws NiPingException {
        String token = null;
        try {
            token = accessCredentialsDao.getTokenByAccountId(accountId);
        } catch (DBIException e) {
            log.error("user auth: get token by account id {} error: {}", accountId, ExceptionUtils.getMessage(e));
            throw new NiPingException(DBError);
        }
        return token;
    }
}