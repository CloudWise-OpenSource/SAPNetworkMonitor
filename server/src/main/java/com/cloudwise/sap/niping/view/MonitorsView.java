package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.monitorPage;

public class MonitorsView extends ResultView {

    public MonitorsView(Result result, Object data) {
        super(result, data, monitorPage.getTemplateName());
    }

    public MonitorsView(NiPingException exception, Object data) {
        super(exception, data, monitorPage.getTemplateName());
    }
}