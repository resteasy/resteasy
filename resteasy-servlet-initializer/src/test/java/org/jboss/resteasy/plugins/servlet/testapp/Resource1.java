package org.jboss.resteasy.plugins.servlet.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/path1")
public class Resource1 {
    @GET
    public String hello() {
        return "Hello";
    }
}