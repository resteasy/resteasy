package org.jboss.resteasy.plugins.providers.jackson;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.resteasy.util.HttpResponseCodes;

/**
 * (RESTEASY-1485) Address concerns of a possible XSS attack by removing some
 * details of the exception.
 *
 * User: rsearls
 * Date: 9/22/16
 */
@Provider
public class UnrecognizedPropertyExceptionHandler implements ExceptionMapper<UnrecognizedPropertyException> {
    @Override
    public Response toResponse(UnrecognizedPropertyException exception)
    {
        return Response.status(HttpResponseCodes.SC_BAD_REQUEST)
            .type(MediaType.TEXT_HTML)
            .entity(exception.getOriginalMessage())
            .build();
    }
}
