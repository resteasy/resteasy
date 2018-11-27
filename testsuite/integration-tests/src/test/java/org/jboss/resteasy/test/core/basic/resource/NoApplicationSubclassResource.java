package org.jboss.resteasy.test.core.basic.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class NoApplicationSubclassResource {

   @GET
   @Produces("application/json")
   @Path("/hello")
   public String getJson() {
      return "hello world";
   }
}
