package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/interception")
public class PreProcessorExceptionMapperResource {
   @GET
   @Produces("text/plain")
   public String get() {
      return "hello world";
   }
}
