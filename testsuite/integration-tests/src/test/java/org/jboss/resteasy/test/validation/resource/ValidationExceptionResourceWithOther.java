package org.jboss.resteasy.test.validation.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ValidationExceptionResourceWithOther {
   @PathParam("s")
   @ValidationExceptionOtherConstraint
   String s;

   @POST
   @Path("parameter/{s}")
   public Response testParameter(@ValidationExceptionOtherConstraint String s) {
      return Response.ok().build();
   }

   @POST
   @Path("return/{s}")
   @ValidationExceptionOtherConstraint
   public String testReturnValue() {
      return "abc";
   }

   @GET
   @Path("execution/{s}")
   public void testExecution() {
      throw new ValidationExceptionOtherValidationException(new ValidationExceptionOtherValidationException2(new ValidationExceptionOtherValidationException3()));
   }
}
