package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.historyPage;

public class HistoryView extends ResultView {
    public HistoryView(Result result, Object data) {
        super(result, data, historyPage.getTemplateName());
    }

    public HistoryView(NiPingException exception, Object data) {
        super(exception, data, historyPage.getTemplateName());
    }
}