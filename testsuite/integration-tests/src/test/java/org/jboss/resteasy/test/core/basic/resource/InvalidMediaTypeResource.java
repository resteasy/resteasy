package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("test")
public class InvalidMediaTypeResource {
   @GET
   @Produces("*/*")
   public Response test() {
      return Response.ok().entity("ok").build();
   }
}
