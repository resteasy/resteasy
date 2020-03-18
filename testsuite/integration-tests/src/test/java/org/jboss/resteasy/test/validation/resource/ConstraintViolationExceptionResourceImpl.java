package org.jboss.resteasy.test.validation.resource;

import javax.ejb.Stateless;
import javax.validation.constraints.Size;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Stateless
public class ConstraintViolationExceptionResourceImpl implements ConstraintViolationExceptionResource {

   @GET
   public Response validate(@Size(min=3) @PathParam("p") String p) {
      return Response.ok(p).build();
   }
}
