package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/my")
public class ApplicationConfigResource {
   @GET
   @Produces("text/quoted")
   public String get() {
      return "hello";
   }
}
