package com.cloudwise.sap.niping.service;

import com.cloudwise.sap.niping.common.entity.AccessCredentials;
import com.cloudwise.sap.niping.common.utils.HashStrategyUtil;
import com.cloudwise.sap.niping.common.vo.User;
import com.cloudwise.sap.niping.dao.AccessCredentialsDao;
import com.cloudwise.sap.niping.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Inject
    private AccessCredentialsDao accessCredentialsDao;

    @Inject
    private UserDao userDao;

    public Optional<AccessCredentials> getCredentialsByToken(String credentials) {
        return Optional.ofNullable(accessCredentialsDao.getCredentialsByToken(credentials));
    }

    public Optional<User> getUser(String loginName, String password) {
        User user = userDao.getUser(loginName);
        try {
            if (null == user || !user.getPassword().equals(HashStrategyUtil.computeHash(password, user.getPasswordSalt()))) {
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("validate user computeHash error: {}", ExceptionUtils.getMessage(e));
        }
        return Optional.of(user);
    }
}