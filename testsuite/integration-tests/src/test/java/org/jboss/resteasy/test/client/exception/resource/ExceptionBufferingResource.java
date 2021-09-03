package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ExceptionBufferingResource {
   @GET
   @Path("test")
   public String test() {
      Response response = Response.serverError().entity("test").build();
      throw new WebApplicationException(response);
   }
}
