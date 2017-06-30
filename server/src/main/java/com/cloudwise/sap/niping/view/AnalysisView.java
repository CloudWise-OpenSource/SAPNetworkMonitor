package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.analysisPage;

public class AnalysisView extends ResultView {
    public AnalysisView(Result result, Object data) {
        super(result, data, analysisPage.getTemplateName());
    }

    public AnalysisView(NiPingException exception, Object data) {
        super(exception, data, analysisPage.getTemplateName());
    }
}