package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/my")
public class ApplicationConfigResource {
   @GET
   @Produces("text/quoted")
   public String get() {
      return "hello";
   }
}
