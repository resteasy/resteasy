package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;

public class PathLimitedBasicResource {
    @GET
    public String hello() {
        return "hello world";
    }
}
