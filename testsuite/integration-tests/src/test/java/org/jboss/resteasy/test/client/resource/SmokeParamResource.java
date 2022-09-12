package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/foo")
public class SmokeParamResource {

   @POST
   @Produces("text/plain")
   @Consumes("text/plain")
   public String create(String cust) {
      return cust;
   }

   @GET
   @Produces("text/plain")
   public String get(@HeaderParam("a") String a, @QueryParam("b") String b) {
      return a + " " + b;
   }

}
