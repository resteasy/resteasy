package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/test")
public class IndirectInvocationTestResource {
   @GET
   @Path("/query")
   @Produces("text/plain")
   public String get(@QueryParam("param") String p, @QueryParam("id") String id) {
      return p + " " + id;
   }

   @POST
   @Path("/send")
   @Consumes("text/plain")
   public String post(@QueryParam("param") String p, @QueryParam("id") String id, String str) {
      return str;
   }
}
