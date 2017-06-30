package com.cloudwise.sap.niping.view;

import lombok.Getter;

public enum ViewTemplateMap {
    indexPage("index.mustache"),
    loginPage("login.mustache"),
    taskAddPage("taskAdd.mustache"),
    taskListPage("taskList.mustache"),
    taskEditPage("taskEdit.mustache"),
    analysisListPage("analysisList.mustache"),
    analysisPage("analysis.mustache"),
    historyPage("history.mustache"),
    historyListPage("historyList.mustache"),
    monitorPage("monitor.mustache"),
    installPage("install.mustache");

    @Getter
    private String templateName;

    ViewTemplateMap(String templateName) {
        this.templateName = templateName;
    }
}