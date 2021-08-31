package org.jboss.resteasy.test.warning.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Produces;

/**
 * Created by rsearls on 9/5/17.
 */
@Path("test")
public class TestResource1 {

   @GET
   @Path("x")
   @Produces("text/plain")
   public Response method() {
      return Response.ok("ok").build();
   }
}
