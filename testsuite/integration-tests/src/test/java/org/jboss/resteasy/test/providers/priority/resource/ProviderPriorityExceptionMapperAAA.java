package org.jboss.resteasy.test.providers.priority.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ProviderPriorityExceptionMapperAAA implements ExceptionMapper<ProviderPriorityTestException> {

   @Override
   public Response toResponse(ProviderPriorityTestException exception) {
      return Response.ok().status(444).entity("AAA").build();
   }
}
