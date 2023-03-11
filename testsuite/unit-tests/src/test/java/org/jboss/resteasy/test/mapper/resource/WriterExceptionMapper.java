package org.jboss.resteasy.test.mapper.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.WriterException;

@Provider
public class WriterExceptionMapper implements ExceptionMapper<WriterException> {
    public static int STATUS_CODE = 100302;

    public Response toResponse(WriterException e) {
        return Response.status(STATUS_CODE).entity(e.getMessage()).build();
    }
}
