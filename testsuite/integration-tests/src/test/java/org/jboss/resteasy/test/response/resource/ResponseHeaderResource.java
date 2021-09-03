package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;


@Path("/test")
public class ResponseHeaderResource {
   @GET
   @Produces("text/plain")
   public String get() {
      throw new ResponseHeaderExceptionMapperRuntimeException();
   }
}
