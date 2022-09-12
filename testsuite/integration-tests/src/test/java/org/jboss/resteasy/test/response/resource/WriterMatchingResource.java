package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class WriterMatchingResource {
   @GET
   @Path("bool")
   public Boolean responseOk() {
      return true;
   }
}
