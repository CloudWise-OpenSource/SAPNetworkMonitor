package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import io.dropwizard.jersey.sessions.Session;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AuthFilterDynamicBinding implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        Method method = resourceInfo.getResourceMethod();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (Annotation[] annotations : parameterAnnotations) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(Session.class) && !method.getName().startsWith("log")) {
                    context.register(NiPingAuthFilter.class);
                }
            }
        }
    }
}