package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperRuntimeExceptionWithReasonMapper implements ExceptionMapper<RuntimeException> {

    public static final String REASON = "Test error occurred";
    @Override
    public Response toResponse(RuntimeException exception) {
        return Response.status(Response.Status.NOT_ACCEPTABLE).entity(REASON).build();
    }
}
