package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.installPage;

public class InstallView extends ResultView {
    public InstallView(Result result, Object data) {
        super(result, data, installPage.getTemplateName());
    }

    public InstallView(NiPingException exception, Object data) {
        super(exception, data, installPage.getTemplateName());
    }
}