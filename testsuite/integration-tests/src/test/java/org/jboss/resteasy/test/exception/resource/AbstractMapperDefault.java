package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AbstractMapperDefault extends AbstractMapper<RuntimeException> {
    @Override
    protected void handleError(final Response.ResponseBuilder builder, final RuntimeException e) {
        builder.entity("default").type(MediaType.TEXT_HTML_TYPE);
    }
}
