package org.jboss.resteasy.test.request.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class AcceptMultipleResource {
    @Produces({"application/foo", "application/bar"})
    @GET
    public String get() {
        return "GET";
    }
}
