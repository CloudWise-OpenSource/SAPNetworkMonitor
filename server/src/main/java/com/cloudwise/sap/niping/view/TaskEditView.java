package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.taskEditPage;

public class TaskEditView extends ResultView {

    public TaskEditView(Result result, Object data) {
        super(result, data, taskEditPage.getTemplateName());
    }

    public TaskEditView(NiPingException exception, Object data) {
        super(exception, data, taskEditPage.getTemplateName());
    }
}