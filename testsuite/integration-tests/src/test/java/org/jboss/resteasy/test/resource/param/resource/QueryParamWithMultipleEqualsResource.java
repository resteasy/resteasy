package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

@Path("/")
public class QueryParamWithMultipleEqualsResource {
   @Path("test")
   @GET
   public String test(@QueryParam("foo") String incoming) {
      return incoming;
   }
}
