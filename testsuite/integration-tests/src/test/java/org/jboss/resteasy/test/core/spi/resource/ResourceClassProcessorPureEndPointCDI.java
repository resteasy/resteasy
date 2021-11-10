package org.jboss.resteasy.test.core.spi.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/pure")
public class ResourceClassProcessorPureEndPointCDI {
   @GET
   @Path("pure")
   @Produces("text/plain")
   public String getLocating() {
      return "<a></a>";
   }
}
