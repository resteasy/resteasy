package org.jboss.resteasy.test.mapper.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.ReaderException;

@Provider
public class ReaderExceptionMapper implements ExceptionMapper<ReaderException> {
    public static int STATUS_CODE = 100402;

    public Response toResponse(ReaderException e) {
        return Response.status(STATUS_CODE).entity(e.getMessage()).build();
    }
}
