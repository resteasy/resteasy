package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/test")
public class IndirectInvocationTestResource {
    @GET
    @Path("/query")
    @Produces("text/plain")
    public String get(@QueryParam("param") String p, @QueryParam("id") String id) {
        return p + " " + id;
    }

    @POST
    @Path("/send")
    @Consumes("text/plain")
    public String post(@QueryParam("param") String p, @QueryParam("id") String id, String str) {
        return str;
    }
}
