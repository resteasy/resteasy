package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("resource")
public class DuplicateDeploymentResource {
   @GET
   @Produces("text/plain")
   public String get() {
      return "hello world";
   }
}
