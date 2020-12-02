package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("resource/subresource")
public class ResourceMatchingMainSubResource {
   public static final String ID = "subresource";

   @GET
   public String subresource() {
      return this.getClass().getSimpleName();
   }

   @POST
   @Path("sub")
   @Consumes(MediaType.TEXT_PLAIN)
   @Produces(MediaType.TEXT_PLAIN)
   public String sub() {
      return this.getClass().getSimpleName();
   }

   @GET
   @Path("{id}")
   public String neverHere() {
      return ID;
   }

   @POST
   @Path("consumes")
   @Consumes(MediaType.TEXT_PLAIN)
   public String consumes() {
      return getClass().getSimpleName();
   }

   @Path("consumeslocator")
   public ResourceMatchingAnotherResourceLocator consumeslocator() {
      return new ResourceMatchingAnotherResourceLocator();
   }

   @POST
   @Path("produces")
   @Produces(MediaType.TEXT_PLAIN)
   public String produces() {
      return getClass().getSimpleName();
   }

   @Path("produceslocator")
   public ResourceMatchingAnotherResourceLocator produceslocator() {
      return new ResourceMatchingAnotherResourceLocator();
   }

}
