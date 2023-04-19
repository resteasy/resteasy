package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandlingProvider implements ExceptionMapper<Exception> {
    public Response toResponse(Exception exception) {
        return Response.serverError().entity("Expected exception raised").build();
    }
}
