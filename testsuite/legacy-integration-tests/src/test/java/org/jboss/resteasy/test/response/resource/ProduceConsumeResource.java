package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("resource")
public class ProduceConsumeResource {

    @POST
    @Path("plain")
    @Produces(MediaType.TEXT_PLAIN)
    public String postPlain() {
        return MediaType.TEXT_PLAIN;
    }

    @POST
    @Path("wild")
    public ProduceConsumeData data(ProduceConsumeData data) {
        return data;
    }

    @Path("empty")
    @GET
    public Response entity() {
        return Response.ok().build();
    }


}
