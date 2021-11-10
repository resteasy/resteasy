package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperInjectionCustomSimpleMapper implements ExceptionMapper<ExceptionMapperInjectionException> {
   public Response toResponse(ExceptionMapperInjectionException exception) {
      return null;
   }
}
