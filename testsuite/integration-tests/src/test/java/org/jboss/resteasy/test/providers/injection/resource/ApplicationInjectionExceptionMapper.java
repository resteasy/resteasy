package org.jboss.resteasy.test.providers.injection.resource;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ApplicationInjectionExceptionMapper implements ExceptionMapper<ApplicationInjectionException>
{
   @Context
   ApplicationInjectionApplicationParent application;
   
   @Override
   public Response toResponse(ApplicationInjectionException exception) {
      return Response.ok(exception.getMessage() + "|" + getClass() + ":" + application.getName()).build();
   }
}
