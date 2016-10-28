package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperCustomRuntimeMapper implements ExceptionMapper<RuntimeException> {
    public Response toResponse(RuntimeException exception) {
        return Response.serverError().build();
    }
}
