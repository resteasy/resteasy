package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class ExceptionMapperCustomRuntimeResource {
   @GET
   @Produces("text/plain")
   public String get() {
      throw new ExceptionMapperCustomRuntimeException();
   }
}
