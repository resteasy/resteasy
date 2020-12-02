package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class JsonBindingDebugLoggingEndPoint {

   @GET
   @Path("/get/ok")
   @Produces(MediaType.APPLICATION_JSON)
   public JsonBindingDebugLoggingItem getOk() {
      return new JsonBindingDebugLoggingItem().setA(5);
   }

   @GET
   @Path("/get/nok")
   @Produces(MediaType.APPLICATION_JSON)
   public JsonBindingDebugLoggingItemCorruptedGet getNok() {
      return new JsonBindingDebugLoggingItemCorruptedGet().setA(5);
   }

   @POST
   @Path("/post")
   @Consumes(MediaType.APPLICATION_JSON)
   public void post(JsonBindingDebugLoggingItemCorruptedSet item) {
   }
}
