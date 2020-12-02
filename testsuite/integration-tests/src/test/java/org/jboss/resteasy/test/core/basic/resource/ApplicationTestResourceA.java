package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/resources")
public class ApplicationTestResourceA {
   @Path("a")
   @GET
   public String get() {
      return "a";
   }
}
