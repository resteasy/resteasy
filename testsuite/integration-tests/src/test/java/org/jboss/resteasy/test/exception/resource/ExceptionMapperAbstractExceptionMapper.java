package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public abstract class ExceptionMapperAbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E> {

    @Override
    public Response toResponse(final E exception) {
        final Response.ResponseBuilder builder = Response.ok();
        handleError(builder, exception);
        return builder.build();
    }

    protected abstract void handleError(Response.ResponseBuilder builder, E e);
}
