package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/regression")
public class UnauthorizedHttpCodeResource {
   @GET
   public Response get() {
      return Response.status(401).entity("hello").type("application/error").build();
   }
}
