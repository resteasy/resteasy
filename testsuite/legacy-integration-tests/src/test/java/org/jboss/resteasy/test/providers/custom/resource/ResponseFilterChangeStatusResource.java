package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
@Produces("text/plain")
public class ResponseFilterChangeStatusResource {

    @POST
    @Path("empty")
    public void empty() {
    }

    @GET
    @Path("default_head")
    public Response defaultHead() {
        return Response.ok(" ").build();
    }
}
