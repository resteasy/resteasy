package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link ForbiddenException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyForbiddenException extends ForbiddenException implements WebApplicationExceptionWrapper<ForbiddenException> {

   private static final long serialVersionUID = -581285336820307590L;
   private final ForbiddenException wrapped;
   private final Response sanitizedResponse;

    ResteasyForbiddenException(final ForbiddenException wrapped) {
        super(wrapped.getMessage(), wrapped.getResponse(), wrapped.getCause());
        this.wrapped = wrapped;
        this.sanitizedResponse = sanitize(wrapped.getResponse());
    }

    @Override
    public ForbiddenException unwrap() {
        return wrapped;
    }

    @Override
    public Response getSanitizedResponse() {
        return sanitizedResponse;
    }
}