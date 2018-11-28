package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/singletons")
public class ApplicationTestSingletonA {

   @Path("a")
   @GET
   public String get() {
      return "a";
   }
}
