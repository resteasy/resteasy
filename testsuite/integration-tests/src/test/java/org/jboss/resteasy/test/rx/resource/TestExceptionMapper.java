package org.jboss.resteasy.test.rx.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TestExceptionMapper implements ExceptionMapper<TestException>{

   @Override
   public Response toResponse(TestException exception) {
      return Response.status(444).entity(exception.getMessage()).build();
   }
}
