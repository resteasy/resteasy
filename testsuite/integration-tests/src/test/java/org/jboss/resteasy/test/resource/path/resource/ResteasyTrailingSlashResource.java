package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class ResteasyTrailingSlashResource {
   @GET
   @Path("/test/")
   @Produces(MediaType.TEXT_PLAIN)
   public String get() {
      return "hello world";
   }
}
