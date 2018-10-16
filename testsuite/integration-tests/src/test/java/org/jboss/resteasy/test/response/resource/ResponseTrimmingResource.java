package org.jboss.resteasy.test.response.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
