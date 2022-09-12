package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("resource")
public class ProduceConsumeResource {

   @POST
   @Path("plain")
   @Produces(MediaType.TEXT_PLAIN)
   public String postPlain() {
      return MediaType.TEXT_PLAIN;
   }

   @POST
   @Path("wild")
   public ProduceConsumeData data(ProduceConsumeData data) {
      return data;
   }

   @Path("empty")
   @GET
   public Response entity() {
      return Response.ok().build();
   }


}
