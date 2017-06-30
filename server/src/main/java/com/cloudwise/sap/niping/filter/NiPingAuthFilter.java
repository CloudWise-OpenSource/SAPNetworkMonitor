package com.cloudwise.sap.niping.filter;

import com.cloudwise.sap.niping.common.vo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;

@Provider
public class NiPingAuthFilter implements ContainerRequestFilter {

    private static final String attributeName = "niping";

    @Context
    private HttpServletRequest httpServletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        final HttpSession session = httpServletRequest.getSession(false);
        if (null == session || null == session.getAttribute(attributeName)) {
            requestContext.abortWith(Response.seeOther(URI.create("/")).build());
        }
    }

    public static void setSession(HttpSession session, User user) {
        session.setAttribute(attributeName, user);
    }

    public static void removeSession(HttpSession session) {
        session.removeAttribute(attributeName);
    }

    public static User getUser(HttpSession session) {
        return (User)(session.getAttribute(attributeName));
    }

    public static String getAccountId(HttpSession session) {
        return ((User)(session.getAttribute(attributeName))).getAccountId();
    }
}