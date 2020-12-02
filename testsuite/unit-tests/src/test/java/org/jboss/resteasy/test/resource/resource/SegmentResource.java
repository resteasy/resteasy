package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/resource")
public class SegmentResource {
   @GET
   @Path("sub")
   public String get() {
      return null;
   }

   @Path("{id}")
   public SegmentLocator locator() {
      return new SegmentLocator();
   }

}
