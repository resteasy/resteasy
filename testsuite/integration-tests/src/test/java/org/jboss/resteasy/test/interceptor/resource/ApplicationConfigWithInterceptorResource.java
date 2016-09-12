package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/my")
public class ApplicationConfigWithInterceptorResource
{
   @GET
   @Produces("text/plain")
   @Path("/good")
   public String get()
   {
      return "hello";
   }

   @GET
   @Produces("text/plain")
   @Path("/bad")
   public String response()
   {
      throw new WebApplicationException(Response.status(Status.CONFLICT).entity("conflicted").build());
   }

   @DELETE
   @Path("{id}")
   public void remove(@PathParam("id") String id)
   {
      return;
   }
}
