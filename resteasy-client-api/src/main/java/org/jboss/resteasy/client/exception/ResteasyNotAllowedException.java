package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link NotAllowedException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotAllowedException extends NotAllowedException implements WebApplicationExceptionWrapper<NotAllowedException> {

   private static final long serialVersionUID = -6319306078354018353L;
   private final NotAllowedException wrapped;
   private final Response sanitizedResponse;

    ResteasyNotAllowedException(final NotAllowedException wrapped) {
        super(wrapped.getMessage(), wrapped.getResponse(), wrapped.getCause());
        this.wrapped = wrapped;
        this.sanitizedResponse = sanitize(wrapped.getResponse());
    }

    @Override
    public NotAllowedException unwrap() {
        return wrapped;
    }

    @Override
    public Response getSanitizedResponse() {
        return sanitizedResponse;
    }
}