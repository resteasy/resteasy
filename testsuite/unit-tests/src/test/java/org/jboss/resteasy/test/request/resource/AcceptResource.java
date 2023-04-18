package org.jboss.resteasy.test.request.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public class AcceptResource {
    @Produces("application/foo")
    @GET
    public String doGetFoo() {
        return "foo";
    }

    @Produces("application/bar")
    @GET
    public String doGetBar() {
        return "bar";
    }

    @Produces("application/baz")
    @GET
    public String doGetBaz() {
        return "baz";
    }

    @Produces("*/*")
    @GET
    public Response doGetWildCard() {
        return Response.ok("wildcard", "application/wildcard").build();
    }
}
