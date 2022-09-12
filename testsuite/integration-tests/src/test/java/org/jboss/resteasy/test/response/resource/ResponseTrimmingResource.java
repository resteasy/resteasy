package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class ResponseTrimmingResource {

   @GET
   @Path("/json")
   @POST
   @Consumes({"application/json"})
   @Produces({"application/json"})
   public String getJSON(int n) {
      return "{\"result\":\"" + n + "\"}";
   }
}
