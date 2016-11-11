package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperMyCustomExceptionMapper extends ExceptionMapperAbstractExceptionMapper<ExceptionMapperMyCustomException> {
    @Override
    protected void handleError(final Response.ResponseBuilder builder, final ExceptionMapperMyCustomException e) {
        builder.entity("custom").type(MediaType.TEXT_HTML_TYPE);
    }
}
