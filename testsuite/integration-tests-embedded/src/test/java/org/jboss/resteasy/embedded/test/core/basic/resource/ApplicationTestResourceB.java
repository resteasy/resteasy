package org.jboss.resteasy.embedded.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/resources")
public class ApplicationTestResourceB {
   @Path("b")
   @GET
   public String get() {
      return "b";
   }
}
