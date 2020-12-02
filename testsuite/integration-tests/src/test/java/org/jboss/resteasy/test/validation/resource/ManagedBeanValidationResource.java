package org.jboss.resteasy.test.validation.resource;

import javax.annotation.ManagedBean;
import javax.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

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