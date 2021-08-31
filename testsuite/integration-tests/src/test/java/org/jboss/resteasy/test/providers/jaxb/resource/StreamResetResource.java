package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class StreamResetResource {
   @GET
   @Produces("application/xml")
   public String get() {
      return "<person name=\"bill\"/>";
   }
}
