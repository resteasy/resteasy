package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Response;

/**
 * Wraps a {@link NotSupportedException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotSupportedException extends NotSupportedException implements WebApplicationExceptionWrapper<NotSupportedException> {
    private final NotSupportedException wrapped;

    ResteasyNotSupportedException(final NotSupportedException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public NotSupportedException unwrap() {
        return wrapped;
    }
}
