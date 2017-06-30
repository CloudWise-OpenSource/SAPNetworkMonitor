package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.indexPage;

public class IndexView extends ResultView {

    public IndexView(Result result, Object data) {
        super(result, data, indexPage.getTemplateName());
    }

    public IndexView(NiPingException exception, Object data) {
        super(exception, data, indexPage.getTemplateName());
    }
}