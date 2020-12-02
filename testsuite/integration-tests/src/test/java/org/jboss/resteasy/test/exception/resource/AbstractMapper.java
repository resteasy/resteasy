package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

public abstract class AbstractMapper<E extends Throwable> implements ExceptionMapper<E> {

   @Override
   public Response toResponse(final E exception) {
      final Response.ResponseBuilder builder = Response.ok();

      handleError(builder, exception);

      return builder.build();
   }

   protected abstract void handleError(Response.ResponseBuilder builder, E e);
}
