package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;

@Path("/test")
public class ExceptionMapperWebRuntimeExceptionResource {
   @GET
   @Produces("text/plain")
   public String get() {
      throw new WebApplicationException(401);
   }

   @GET
   @Path("failure")
   @Produces("text/plain")
   public String getFailure() {
      return "hello";
   }
}
