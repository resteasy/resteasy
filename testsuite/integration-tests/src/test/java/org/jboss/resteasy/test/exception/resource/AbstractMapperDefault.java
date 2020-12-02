package org.jboss.resteasy.test.exception.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AbstractMapperDefault extends AbstractMapper<RuntimeException> {
   @Override
   protected void handleError(final Response.ResponseBuilder builder, final RuntimeException e) {
      builder.entity("default").type(MediaType.TEXT_HTML_TYPE);
   }
}
