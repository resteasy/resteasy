package org.jboss.resteasy.test.core.encoding.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/test")
public interface EncodingTestClient {
   @GET
   @Produces("text/plain")
   @Path("/path-param/{pathParam}")
   Response getPathParam(@PathParam("pathParam") String pathParam);


   @GET
   @Produces("text/plain")
   @Path("/query-param")
   Response getQueryParam(@QueryParam("queryParam") String queryParam);
}
