package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resources")
public class ApplicationTestResourceA {
   @Path("a")
   @GET
   public String get() {
      return "a";
   }
}
