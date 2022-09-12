package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link NotSupportedException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotSupportedException extends NotSupportedException implements WebApplicationExceptionWrapper<NotSupportedException> {

   private static final long serialVersionUID = 6195843283913647466L;
   private final NotSupportedException wrapped;
   private final Response sanitizedResponse;

    ResteasyNotSupportedException(final NotSupportedException wrapped) {
        super(wrapped.getMessage(), wrapped.getResponse(), wrapped.getCause());
        this.wrapped = wrapped;
        this.sanitizedResponse = sanitize(wrapped.getResponse());
    }

    @Override
    public NotSupportedException unwrap() {
        return wrapped;
    }

    @Override
    public Response getSanitizedResponse() {
        return sanitizedResponse;
    }
}