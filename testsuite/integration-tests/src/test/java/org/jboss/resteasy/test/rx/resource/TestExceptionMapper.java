package org.jboss.resteasy.test.rx.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class TestExceptionMapper implements ExceptionMapper<TestException>{

   @Override
   public Response toResponse(TestException exception) {
      return Response.status(444).entity(exception.getMessage()).build();
   }
}
