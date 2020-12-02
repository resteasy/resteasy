package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class InputStreamResource {
   @Path("test")
   @Produces("text/plain")
   @GET
   public String get() {
      return "hello world";
   }
}
