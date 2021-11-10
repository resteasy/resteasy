package org.jboss.resteasy.test.client.other.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public interface ApacheHttpClient4Resource {
   @GET
   @Produces("text/plain")
   String get();

   @GET
   @Path("error")
   @Produces("text/plain")
   String error();

   @POST
   @Path("data")
   @Produces("text/plain")
   @Consumes("text/plain")
   String getData(String data);
}
