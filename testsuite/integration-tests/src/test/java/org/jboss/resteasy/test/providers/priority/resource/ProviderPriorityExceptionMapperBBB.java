package org.jboss.resteasy.test.providers.priority.resource;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(20)
public class ProviderPriorityExceptionMapperBBB implements ExceptionMapper<ProviderPriorityTestException> {

   @Override
   public Response toResponse(ProviderPriorityTestException exception) {
      return Response.ok().status(444).entity("BBB").build();
   }
}
