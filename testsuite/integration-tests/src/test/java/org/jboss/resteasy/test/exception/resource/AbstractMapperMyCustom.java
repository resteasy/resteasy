package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AbstractMapperMyCustom extends AbstractMapper<AbstractMapperException> {
   @Override
   protected void handleError(final Response.ResponseBuilder builder, final AbstractMapperException e) {
      builder.entity("custom").type(MediaType.TEXT_HTML_TYPE);
   }
}
