package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.taskListPage;

public class TaskListView extends ResultView {

    public TaskListView(Result result, Object data) {
        super(result, data, taskListPage.getTemplateName());
    }

    public TaskListView(NiPingException exception, Object data) {
        super(exception, data, taskListPage.getTemplateName());
    }
}