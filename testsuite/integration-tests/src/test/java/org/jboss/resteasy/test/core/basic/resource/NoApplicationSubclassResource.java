package org.jboss.resteasy.test.core.basic.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class NoApplicationSubclassResource {

   @GET
   @Produces("application/json")
   @Path("/hello")
   public String getJson() {
      return "hello world";
   }
}
