package org.jboss.resteasy.test.encoding;

import org.jboss.resteasy.client.ClientResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/test")
public interface TestClient
{
   @GET
   @Produces("text/plain")
   @Path("/path-param/{pathParam}")
   public ClientResponse<String> getPathParam(@PathParam("pathParam") String pathParam);



   @GET
   @Produces("text/plain")
   @Path("/query-param")
   public ClientResponse<String> getQueryParam(@QueryParam("queryParam") String queryParam);
}
