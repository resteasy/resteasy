package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/** ExceptionHandler only uses the logger for tracing exception mapping, so we need to map something.
 */
@Provider
public class ClosedResponseHandlingPleaseMapExceptionMapper implements ExceptionMapper<ClosedResponseHandlingPleaseMapException> {

   @Override
   public Response toResponse(ClosedResponseHandlingPleaseMapException e) {
      return e.response;
   }
}
