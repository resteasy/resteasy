package org.jboss.resteasy.test.exception.resource;

import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Path("/")
public class WebApplicationExceptionResource {
    @Path("/exception")
    @GET
    public Response get() throws WebApplicationException {
        throw new WebApplicationException(Response.status(HttpResponseCodes.SC_UNAUTHORIZED).build());
    }

    @Path("/exception/entity")
    @GET
    public Response getEntity() throws WebApplicationException {
        throw new WebApplicationException(Response.status(HttpResponseCodes.SC_UNAUTHORIZED).entity("error").build());
    }
}
