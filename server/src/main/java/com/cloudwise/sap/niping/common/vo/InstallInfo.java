package com.cloudwise.sap.niping.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InstallInfo {

    private String apiUrl;
    private String token;

}