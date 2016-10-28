package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class AbstractMapperMyCustom extends AbstractMapper<AbstractMapperException> {
    @Override
    protected void handleError(final Response.ResponseBuilder builder, final AbstractMapperException e) {
        builder.entity("custom").type(MediaType.TEXT_HTML_TYPE);
    }
}
