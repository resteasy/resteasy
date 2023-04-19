package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;

public class LocatorTestLocator2 {
    @GET
    public String ok() {
        return "ok";
    }
}
