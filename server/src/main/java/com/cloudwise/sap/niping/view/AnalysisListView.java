package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.analysisListPage;

public class AnalysisListView extends ResultView {
    public AnalysisListView(Result result, Object data) {
        super(result, data, analysisListPage.getTemplateName());
    }

    public AnalysisListView(NiPingException exception, Object data) {
        super(exception, data, analysisListPage.getTemplateName());
    }
}