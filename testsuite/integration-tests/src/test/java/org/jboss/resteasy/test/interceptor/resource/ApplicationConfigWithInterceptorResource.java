package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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
