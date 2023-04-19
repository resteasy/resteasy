package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapperWebAppExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        // When not found, i.e. url is wrong, one get also
        // WebApplicationException
        if (exception.getClass() != WebApplicationException.class) {
            return exception.getResponse();
        }
        return Response.status(Response.Status.ACCEPTED).build();
    }

}
