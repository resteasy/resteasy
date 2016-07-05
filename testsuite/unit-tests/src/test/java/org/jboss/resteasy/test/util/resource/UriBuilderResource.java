package org.jboss.resteasy.test.util.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("resource")
public class UriBuilderResource {
    @Path("method")
    @GET
    public String get() {
        return "";
    }

    @Path("locator")
    public Object locator() {
        return null;
    }
}
