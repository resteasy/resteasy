package org.jboss.resteasy.test.request.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class AcceptComplexResource {
    @Consumes("text/*")
    @Produces("text/html")
    @GET
    public String method1() {
        return null;
    }

    @Consumes("text/xml")
    @Produces("text/json")
    @GET
    public String method2() {
        return null;
    }
}
