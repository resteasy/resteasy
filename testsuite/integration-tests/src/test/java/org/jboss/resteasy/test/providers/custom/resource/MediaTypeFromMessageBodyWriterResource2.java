package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("")
public class MediaTypeFromMessageBodyWriterResource2 {

   @GET
   public Response getJson() {
      return Response.ok().entity(new CustomProviderPreferenceUser("dummy", "dummy@dummy.com")).build();
   }
}
