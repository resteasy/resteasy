package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class PreProcessorExceptionMapperRuntimeExceptionMapper implements ExceptionMapper<RuntimeException> {
    public Response toResponse(RuntimeException exception) {
        return Response.status(412).build();
    }
}
