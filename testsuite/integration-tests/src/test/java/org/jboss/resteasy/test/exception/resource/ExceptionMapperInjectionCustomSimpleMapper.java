package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ExceptionMapperInjectionCustomSimpleMapper implements ExceptionMapper<ExceptionMapperInjectionException> {
    public Response toResponse(ExceptionMapperInjectionException exception) {
        return null;
    }
}
