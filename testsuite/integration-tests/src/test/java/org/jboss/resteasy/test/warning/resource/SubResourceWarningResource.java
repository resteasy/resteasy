package org.jboss.resteasy.test.warning.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * Created by rsearls on 9/11/17.
 */
@Path("test")
public class SubResourceWarningResource {

   @GET
   @Path("get")
   public Response getCookie() {
      TestSubResource sr = new TestSubResource();
      return Response.ok().entity(sr).build();
   }

   @Path("get")
   public String testString() {
      return "My test sub-locator.";
   }

}
