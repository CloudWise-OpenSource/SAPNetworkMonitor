package com.cloudwise.sap.niping.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Monitor extends AbstractEntity {

    private static final long serialVersionUID = 6732242760258530600L;
    private String monitorId;
    private String name;
    private String country;
    private String province;
    private String city;
    private String isp;
    private String area;
    private String ip;
    private String nipingT;
    private int status;
    private List<String> runningTaskIds;

    public enum Status {
        active(1), inactive(0);

        @Getter
        private int status;

        Status(int status) {this.status = status;}
    }
}