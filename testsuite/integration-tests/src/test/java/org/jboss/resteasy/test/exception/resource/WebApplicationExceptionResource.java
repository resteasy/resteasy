package org.jboss.resteasy.test.exception.resource;

import org.jboss.resteasy.spi.HttpResponseCodes;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

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
