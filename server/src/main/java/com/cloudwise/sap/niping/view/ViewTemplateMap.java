package com.cloudwise.sap.niping.view;

import lombok.Getter;

public enum ViewTemplateMap {
    TaskListPage("index.mustache");

    @Getter
    private String templateName;

    ViewTemplateMap(String templateName) {
        this.templateName = templateName;
    }
}