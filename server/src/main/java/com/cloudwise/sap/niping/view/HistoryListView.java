package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.historyListPage;

public class HistoryListView extends ResultView {
    public HistoryListView(Result result, Object data) {
        super(result, data, historyListPage.getTemplateName());
    }

    public HistoryListView(NiPingException exception, Object data) {
        super(exception, data, historyListPage.getTemplateName());
    }
}