package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
@Produces("text/plain")
@Consumes("text/plain")
public class PrimitiveResource {
    @POST
    @Path("int")
    public int postInt(int val) {
        return val;
    }

    @POST
    @Path("boolean")
    public boolean postInt(boolean val) {
        return val;
    }

    @GET
    @Path("nothing")
    public Response nothing() {
        return Response.ok().build();
    }
}
