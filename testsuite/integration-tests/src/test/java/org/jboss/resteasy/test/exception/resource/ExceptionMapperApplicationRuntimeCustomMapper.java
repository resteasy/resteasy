package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.spi.ApplicationException;

public class ExceptionMapperApplicationRuntimeCustomMapper implements ExceptionMapper<ApplicationException> {
    public Response toResponse(ApplicationException exception) {
        return Response.status(Response.Status.PRECONDITION_FAILED).build();
    }
}
