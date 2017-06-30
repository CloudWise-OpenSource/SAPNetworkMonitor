package com.cloudwise.sap.niping.resource;

import com.cloudwise.sap.niping.view.LoginView;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.cloudwise.sap.niping.common.constant.Result.SUCCESS;

@Slf4j
@Path("/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.TEXT_HTML)
public class WelcomeResource {

    @GET
    @Path("/")
    @Produces(MediaType.TEXT_HTML)
    public LoginView welcome() {
        return new LoginView(SUCCESS, null);
    }
}