package com.cloudwise.sap.niping.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasicAuthUser extends UserPrincipal {
    private String userId;
    private String loginName;
    protected String accountId;
    protected String name;
}