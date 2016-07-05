package org.jboss.resteasy.test.request.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class AcceptConsumeResource {
    @Consumes("application/foo")
    @GET
    public String doGetFoo() {
        return "foo";
    }

    @Consumes("application/bar")
    @GET
    public String doGetBar() {
        return "bar";
    }

    @Consumes("application/baz")
    @GET
    public String doGetBaz() {
        return "baz";
    }

    @Consumes("*/*")
    @GET
    public Response doGetWildCard() {
        return Response.ok("wildcard", "application/wildcard").build();
    }
}
