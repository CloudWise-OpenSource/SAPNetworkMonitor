package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.common.constant.Result;
import com.cloudwise.sap.niping.common.vo.User;
import com.cloudwise.sap.niping.common.vo.converter.TaskConverter;
import com.cloudwise.sap.niping.exception.NiPingException;
import com.cloudwise.sap.niping.filter.NiPingAuthFilter;
import com.cloudwise.sap.niping.service.AuthService;
import com.cloudwise.sap.niping.service.TaskService;
import com.cloudwise.sap.niping.view.IndexView;
import com.cloudwise.sap.niping.view.LoginView;
import com.cloudwise.sap.niping.view.ResultView;
import io.dropwizard.jersey.sessions.Session;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Optional;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Path("/api/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexResource {

    @Inject
    TaskConverter taskConverter;
    @Inject
    private AuthService authService;
    @Inject
    private TaskService taskService;

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public IndexView gotoIndex(@Session HttpSession session) {
        try {
            return new IndexView(SUCCESS, taskConverter.convert(taskService.listTasksForListPage(NiPingAuthFilter.getAccountId(session))).orElse(null));
        } catch (NiPingException e) {
            return new IndexView(e, null);
        }
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public LoginView gotoLogin() {
        return new LoginView(SUCCESS, null);
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_HTML)
    public LoginView logout(@Session HttpSession session, @Context HttpServletResponse response) {
        if (null != session) {
            NiPingAuthFilter.removeSession(session);
        }
        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LoginView(SUCCESS, null);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public ResultView login(@FormParam("loginName") String loginName, @FormParam("password") String password, @Session HttpSession session, @Context HttpServletResponse response) {
        Optional<User> user = authService.validateUser(loginName, password);
        if (user.isPresent()) {
            NiPingAuthFilter.setSession(session, user.get());
            try {
                response.sendRedirect("/api");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return gotoIndex(session);
        }
        return new LoginView(Result.Error, null);
    }
}
