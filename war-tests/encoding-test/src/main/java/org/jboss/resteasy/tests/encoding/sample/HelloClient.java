package org.jboss.resteasy.tests.encoding.sample;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * @author Edgar Silva
 */
@Path(value = "/sayhello")
public interface HelloClient
{

   @GET
   @Path("/en/{in}")
   @Produces("text/plain")
   public String sayHi(@PathParam(value = "in") String in);

   @POST
   @Path("/compile")
   public String compile(@QueryParam("query") String queryText);


}
