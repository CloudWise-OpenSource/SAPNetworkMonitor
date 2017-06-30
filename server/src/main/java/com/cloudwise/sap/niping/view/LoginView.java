package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.exception.NiPingException;

import static com.cloudwise.sap.niping.view.ViewTemplateMap.loginPage;

public class LoginView extends ResultView {
    public LoginView(Result result, Object data) {
        super(result, data, loginPage.getTemplateName());
    }

    public LoginView(NiPingException exception, Object data) {
        super(exception, data, loginPage.getTemplateName());
    }
}