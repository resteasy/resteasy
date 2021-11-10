package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path(value = "/sayhello")
public interface SpecialCharactersProxy {

   @GET
   @Path("/en/{in}")
   @Produces("text/plain")
   String sayHi(@PathParam(value = "in") String in);

   @POST
   @Path("/compile")
   String compile(@QueryParam("query") String queryText);


}
