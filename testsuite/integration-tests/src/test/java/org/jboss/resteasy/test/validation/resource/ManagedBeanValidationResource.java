package org.jboss.resteasy.test.validation.resource;

import javax.annotation.ManagedBean;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("")
@ManagedBean
@Produces("application/json")
@Consumes("application/json")
public class ManagedBeanValidationResource {

   private static boolean visited = false;

   @GET
   @Path("validate")
   public Response validate(@Min(value = 1) @QueryParam("q") int n)
   {
      visited = true;
      return Response.status(Status.OK).entity(n).build();
   }

   @GET
   @Path("visited")
   public Response visited() {
      return Response.ok(visited).build();
   }
}