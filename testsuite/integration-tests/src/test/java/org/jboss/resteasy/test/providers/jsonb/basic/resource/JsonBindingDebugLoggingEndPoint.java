package org.jboss.resteasy.test.providers.jsonb.basic.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
