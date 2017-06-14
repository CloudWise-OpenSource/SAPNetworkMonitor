package com.cloudwise.sap.niping.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUser extends UserPrincipal {
    private String accountId;
    private String name;
}