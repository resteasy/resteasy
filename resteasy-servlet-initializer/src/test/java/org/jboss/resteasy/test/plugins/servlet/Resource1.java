package org.jboss.resteasy.test.plugins.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/path1")
public class Resource1 {
    @GET
    public String hello() {
        return "Hello";
    }
}