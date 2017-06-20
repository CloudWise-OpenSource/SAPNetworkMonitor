package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.TaskListPage;

public class TasksView extends ResultView {

    public TasksView(Result result, Object data) {
        super(result, data, TaskListPage.getTemplateName());
    }

    public TasksView(NiPingException exception, Object data) {
        super(exception, data, TaskListPage.getTemplateName());
    }
}