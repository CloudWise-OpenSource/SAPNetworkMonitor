package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.taskAddPage;

public class TaskAddView extends ResultView {

    public TaskAddView(Result result, Object data) {
        super(result, data, taskAddPage.getTemplateName());
    }

    public TaskAddView(NiPingException exception, Object data) {
        super(exception, data, taskAddPage.getTemplateName());
    }
}