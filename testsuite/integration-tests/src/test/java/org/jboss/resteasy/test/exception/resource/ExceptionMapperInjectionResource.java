package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class ExceptionMapperInjectionResource {
   @GET
   @Produces("text/plain")
   public String get() {
      throw new ExceptionMapperCustomRuntimeException();
   }

   @Path("/null")
   @GET
   @Produces("text/plain")
   public String getNull() {
      throw new ExceptionMapperInjectionException();
   }
}
