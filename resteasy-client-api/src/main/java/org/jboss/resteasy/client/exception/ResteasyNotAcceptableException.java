package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link NotAcceptableException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotAcceptableException extends NotAcceptableException implements WebApplicationExceptionWrapper<NotAcceptableException> {

   private static final long serialVersionUID = 5369100091818187044L;
   private final NotAcceptableException wrapped;
   private final Response sanitizedResponse;

    ResteasyNotAcceptableException(final NotAcceptableException wrapped) {
        super(wrapped.getMessage(), wrapped.getResponse(), wrapped.getCause());
        this.wrapped = wrapped;
        this.sanitizedResponse = sanitize(wrapped.getResponse());
    }

    @Override
    public NotAcceptableException unwrap() {
        return wrapped;
    }

    @Override
    public Response getSanitizedResponse() {
        return sanitizedResponse;
    }
}