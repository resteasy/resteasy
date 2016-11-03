package org.jboss.resteasy.test.providers.jettison.resource;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ContentTypeMatchingErrorExceptionMapper implements ExceptionMapper<ContentTypeMatchingErrorException> {
    public Response toResponse(ContentTypeMatchingErrorException exception) {
        return Response.status(412).entity(new ContentTypeMatchingError()).build();
    }
}
