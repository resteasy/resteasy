package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces("text/plain")
@Consumes("text/plain")
public class PrimitiveResource {
   @POST
   @Path("int")
   public int postInt(int val) {
      return val;
   }

   @POST
   @Path("boolean")
   public boolean postInt(boolean val) {
      return val;
   }

   @GET
   @Path("nothing")
   public Response nothing() {
      return Response.ok().build();
   }
}
