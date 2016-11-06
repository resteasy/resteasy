package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperCustomRuntimeCustomMapper implements ExceptionMapper<ExceptionMapperCustomRuntimeException> {
    public Response toResponse(ExceptionMapperCustomRuntimeException exception) {
        return Response.status(Response.Status.PRECONDITION_FAILED).header("custom", "header").entity("My custom message").build();
    }
}
