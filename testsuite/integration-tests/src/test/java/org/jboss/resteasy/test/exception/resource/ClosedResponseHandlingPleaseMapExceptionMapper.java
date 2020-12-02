package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** ExceptionHandler only uses the logger for tracing exception mapping, so we need to map something.
 */
@Provider
public class ClosedResponseHandlingPleaseMapExceptionMapper implements ExceptionMapper<ClosedResponseHandlingPleaseMapException> {

   @Override
   public Response toResponse(ClosedResponseHandlingPleaseMapException e) {
      return e.response;
   }
}
