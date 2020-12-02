package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("resource/subresource/sub")
public class ResourceMatchingAnotherSubResource {

   @POST
   @Consumes(MediaType.TEXT_PLAIN)
   public String sub() {
      return getClass().getSimpleName();
   }

   @POST
   public String subsub() {
      return sub() + sub();
   }

   @GET
   public String get() {
      return sub();
   }

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getget() {
      return subsub();
   }

   @GET
   @Produces("text/*")
   public String getTextStar() {
      return "text/*";
   }

   @POST
   @Consumes("text/*")
   public String postTextStar() {
      return "text/*";
   }
}
