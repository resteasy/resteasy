package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class ExceptionHandlingResource {
   @Path("test")
   @POST
   public void post() throws Exception {
      throw new Exception("test");
   }
}
