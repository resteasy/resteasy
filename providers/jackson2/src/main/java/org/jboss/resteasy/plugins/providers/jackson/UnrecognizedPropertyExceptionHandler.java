package org.jboss.resteasy.plugins.providers.jackson;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.HttpResponseCodes;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

/**
 * (RESTEASY-1485) Address concerns of a possible XSS attack by removing some
 * details of the exception.
 *
 * User: rsearls
 * Date: 9/22/16
 */
@Provider
public class UnrecognizedPropertyExceptionHandler implements ExceptionMapper<UnrecognizedPropertyException> {
   @Override
   public Response toResponse(UnrecognizedPropertyException exception)
   {
      return Response.status(HttpResponseCodes.SC_BAD_REQUEST)
         .type(MediaType.TEXT_HTML)
         .entity(exception.getOriginalMessage())
         .build();
   }
}
