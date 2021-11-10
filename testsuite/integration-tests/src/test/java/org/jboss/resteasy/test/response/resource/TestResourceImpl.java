package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("test")
public class TestResourceImpl
{
   @GET
   @Path("/document/{documentId}/content")
   @Produces("application/octet-stream")
   public Response readContent(@PathParam("documentId") java.lang.String id)
   {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < 10000000; i++)
      {
         sb.append("a");
      }
      return Response.ok(sb.toString()).build();
   }
}
