package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("error")
public class ResourceMatchingErrorResource {
   @GET
   @Produces("text/*")
   public String test() {
      return getClass().getSimpleName();
   }

   @POST
   @Produces("text/*")
   public Response response(String msg) {
      return Response.ok(msg).build();
   }
}
