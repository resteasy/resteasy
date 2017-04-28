package org.jboss.resteasy.resteasy1630;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 */
@Path("/")
public class TestResource
{
   @GET
   @Path("test/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   public Response test(@Min(7) @PathParam("param") int param)
   {
      System.out.println("param: " + param);
      return Response.ok().entity(param).build();
   }
}
