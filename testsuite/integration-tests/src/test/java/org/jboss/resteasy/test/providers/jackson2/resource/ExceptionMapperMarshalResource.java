package org.jboss.resteasy.test.providers.jackson2.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("resource")
public class ExceptionMapperMarshalResource {
   @GET
   @Path("custom")
   public List<ExceptionMapperMarshalName> custom() throws Throwable {
      throw new ExceptionMapperMarshalMyCustomException("hello");
   }

   @GET
   @Path("customME")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response test() {
      MyEntity entity = new MyEntity();
      return Response.status(Response.Status.OK).entity(entity).build();
   }
}
