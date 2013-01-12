package org.jboss.resteasy.test.nextgen.encoding;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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


   @GET
   @Produces("text/plain")
   @Path("/query-param")
   public String getQueryParam(@QueryParam("queryParam") String queryParam)
   {
      return queryParam;
   }
}
