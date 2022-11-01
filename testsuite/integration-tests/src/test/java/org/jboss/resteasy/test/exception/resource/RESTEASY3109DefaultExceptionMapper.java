package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.core.Response;

@Provider
public class RESTEASY3109DefaultExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(final Throwable exception) {
        return Response.serverError().entity(exception).build();
    }
}
