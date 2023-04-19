package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperWebRuntimeExceptionMapper implements ExceptionMapper<WebApplicationException> {
    public Response toResponse(WebApplicationException exception) {
        return Response.status(Response.Status.PRECONDITION_FAILED).header("custom", "header").build();
    }
}
