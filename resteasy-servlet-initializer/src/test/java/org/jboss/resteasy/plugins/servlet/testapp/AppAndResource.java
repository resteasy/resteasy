package org.jboss.resteasy.plugins.servlet.testapp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/app")
@Path("/path")
public class AppAndResource extends Application {

    @GET
    public String foo() {
        return "foo";
    }
}
