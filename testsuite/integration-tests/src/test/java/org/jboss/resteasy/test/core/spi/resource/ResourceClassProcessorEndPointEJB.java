package org.jboss.resteasy.test.core.spi.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/patched")
public class ResourceClassProcessorEndPointEJB {
   @GET
   @Path("pure")
   @Produces("text/plain")
   public String pure() {
      return "<a></a>";
   }

   @POST // should be replaced by GET in ResourceClassProcessorMethod
   @Path("custom")
   @Produces("text/plain")
   public String custom() {
      return "<a></a>";
   }

}
