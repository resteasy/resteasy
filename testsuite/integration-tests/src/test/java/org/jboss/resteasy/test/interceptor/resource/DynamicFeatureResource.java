package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/dynamic-feature")
public class DynamicFeatureResource {

   @Path("/hello")
   @GET
   @POST
   @Produces("text/plain")
   @Consumes("text/plain")
   public String hello(String name) {
      return name;
   }

}
