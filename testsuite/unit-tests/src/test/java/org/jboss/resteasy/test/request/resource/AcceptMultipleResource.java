package org.jboss.resteasy.test.request.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class AcceptMultipleResource {
   @Produces({"application/foo", "application/bar"})
   @GET
   public String get() {
      return "GET";
   }
}
