package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.spi.ApplicationException;

public class ExceptionMapperApplicationRuntimeCustomMapper implements ExceptionMapper<ApplicationException> {
    public Response toResponse(ApplicationException exception) {
        return Response.status(Response.Status.PRECONDITION_FAILED).build();
    }
}
