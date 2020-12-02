package org.jboss.resteasy.test.providers.priority.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProviderPriorityExceptionMapperAAA implements ExceptionMapper<ProviderPriorityTestException> {

   @Override
   public Response toResponse(ProviderPriorityTestException exception) {
      return Response.ok().status(444).entity("AAA").build();
   }
}
