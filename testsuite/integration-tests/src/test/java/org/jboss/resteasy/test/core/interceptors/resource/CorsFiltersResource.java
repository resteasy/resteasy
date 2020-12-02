package org.jboss.resteasy.test.core.interceptors.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class CorsFiltersResource {
   @Path("test")
   @GET
   @Produces("text/plain")
   public String get() {
      return "hello";
   }
}
