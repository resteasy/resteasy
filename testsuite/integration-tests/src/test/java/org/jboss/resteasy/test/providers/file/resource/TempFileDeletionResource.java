package org.jboss.resteasy.test.providers.file.resource;

import java.io.File;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
