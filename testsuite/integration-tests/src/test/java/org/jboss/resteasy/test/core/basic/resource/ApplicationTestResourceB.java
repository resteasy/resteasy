package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/resources")
public class ApplicationTestResourceB {
   @Path("b")
   @GET
   public String get() {
      return "b";
   }
}
