package org.jboss.resteasy.test.exception.resource;

import org.jboss.resteasy.spi.ApplicationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperApplicationRuntimeCustomMapper implements ExceptionMapper<ApplicationException> {
    public Response toResponse(ApplicationException exception) {
        return Response.status(Response.Status.PRECONDITION_FAILED).build();
    }
}
