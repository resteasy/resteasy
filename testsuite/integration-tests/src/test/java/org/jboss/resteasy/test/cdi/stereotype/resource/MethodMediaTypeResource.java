package org.jboss.resteasy.test.cdi.stereotype.resource;

import org.jboss.resteasy.test.cdi.stereotype.resource.stereotypes.MediaTypeStereotype;

import javax.enterprise.inject.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/mediatype")
public class MethodMediaTypeResource
{
   @GET
   @Path("/produces")
   @MediaTypeStereotype
   @Produces
   public Response produces()
   {
      return Response.ok("{}").build();
   }

   @POST
   @Path("/consumes")
   @MediaTypeStereotype
   @Produces
   public Response consumes()
   {
      return Response.ok().build();
   }
}
