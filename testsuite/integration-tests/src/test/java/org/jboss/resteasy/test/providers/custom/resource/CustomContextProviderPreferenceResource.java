package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("")
@Produces("text/plain")
public class CustomContextProviderPreferenceResource {

   @GET
   @Path("test")
   public Response test() {
      return Response.status(CustomContextProviderPreferenceResolver.entered ? 200 : 444).build();
   }
}
