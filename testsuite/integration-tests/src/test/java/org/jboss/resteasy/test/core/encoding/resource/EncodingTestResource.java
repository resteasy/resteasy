package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("/test")
public class EncodingTestResource {
   @GET
   @Produces("text/plain")
   @Path("/path-param/{pathParam}")
   public String getPathParam(@PathParam("pathParam") String pathParam) {
      return pathParam;
   }


   @GET
   @Produces("text/plain")
   @Path("/query-param")
   public String getQueryParam(@QueryParam("queryParam") String queryParam) {
      return queryParam;
   }
}
