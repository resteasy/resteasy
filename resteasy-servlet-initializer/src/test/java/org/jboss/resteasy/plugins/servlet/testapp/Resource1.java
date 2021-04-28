package org.jboss.resteasy.plugins.servlet.testapp;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/path1")
public class Resource1 {
    @GET
    public String hello() {
        return "Hello";
    }
}
