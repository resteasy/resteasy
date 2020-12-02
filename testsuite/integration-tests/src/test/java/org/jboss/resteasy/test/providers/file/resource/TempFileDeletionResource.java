package org.jboss.resteasy.test.providers.file.resource;

import java.io.File;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/test")
public class TempFileDeletionResource
{
   @POST
   @Path("post")
   public Response post(File file) throws Exception
   {
      return Response.ok(file.getPath()).build();
   }
}
