package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class ResourceMatchingAnotherResourceLocator {

   @GET
   public String get() {
      return getClass().getSimpleName();
   }

   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public String post() {
      return get();
   }

   @DELETE
   public String delete() {
      return get();
   }
}
