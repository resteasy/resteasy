package org.jboss.resteasy.test.encoding;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/test")
public class MyTestResource
{
   @GET
   @Produces("text/plain")
   @Path("/path-param/{pathParam}")
   public String getPathParam(@PathParam("pathParam") String pathParam)
   {
      return pathParam;
   }
}
