package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("resource")
public class SegmentResourceResponse {
   @GET
   @Path("responseok")
   public String responseOk() {
      return "ok";
   }

   @Path("{id}")
   public Object locate(@PathParam("id") int id) {
      return new SegmentLocatorSimple();
   }
}
