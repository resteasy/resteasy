package org.jboss.resteasy.test.client.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/")
public class ExceptionBufferingResource {
    @GET
    @Path("test")
    public String test() {
        Response response = Response.serverError().entity("test").build();
        throw new WebApplicationException(response);
    }
}
