package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/")
@Produces("text/plain")
public class ResponseFilterChangeStatusResource {

   @POST
   @Path("empty")
   public void empty() {
   }

   @GET
   @Path("default_head")
   public Response defaultHead() {
      return Response.ok(" ").build();
   }
}
