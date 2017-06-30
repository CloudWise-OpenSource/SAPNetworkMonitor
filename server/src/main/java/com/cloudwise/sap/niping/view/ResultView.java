package com.cloudwise.sap.niping.view;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.common.vo.RestfulReturnResult;
import com.cloudwise.sap.niping.exception.NiPingException;
import io.dropwizard.views.View;
import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class ResultView extends View {

    @Getter
    private final RestfulReturnResult result;

    public ResultView(Result result, Object data, String templateName) {
        super(templateName, Charset.forName(StandardCharsets.UTF_8.name()));
        this.result = new RestfulReturnResult(result, data);
    }

    public ResultView(NiPingException exception, Object data, String templateName) {
        super(templateName, Charset.forName(StandardCharsets.UTF_8.name()));
        this.result = new RestfulReturnResult(exception, data);
    }
}