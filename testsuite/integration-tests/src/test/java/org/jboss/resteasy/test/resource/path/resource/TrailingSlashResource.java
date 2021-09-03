package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("test")
public class TrailingSlashResource {
   @Context
   private UriInfo uriInfo;

   @GET
   @Produces("text/plain")
   public Response test() {
      return Response.ok(uriInfo.getPath()).build();
   }
}
