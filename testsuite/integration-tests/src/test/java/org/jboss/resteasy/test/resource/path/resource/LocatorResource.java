package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("resource")
public class LocatorResource {
   @GET
   @Path("responseok")
   public String responseOk() {
      return "ok";
   }

   @Path("{id}")
   public Object locate(@PathParam("id") int id) {
      return new LocatorTestLocator2();
   }
}
