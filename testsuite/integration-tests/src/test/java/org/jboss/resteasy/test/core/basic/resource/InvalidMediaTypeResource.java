package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("test")
public class InvalidMediaTypeResource {
   @GET
   @Produces("*/*")
   public Response test() {
      return Response.ok().entity("ok").build();
   }
}
