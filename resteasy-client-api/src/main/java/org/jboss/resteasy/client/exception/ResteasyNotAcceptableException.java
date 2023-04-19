package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Response;

/**
 * Wraps a {@link NotAcceptableException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotAcceptableException extends NotAcceptableException
        implements WebApplicationExceptionWrapper<NotAcceptableException> {

    private static final long serialVersionUID = 5369100091818187044L;
    private final NotAcceptableException wrapped;

    ResteasyNotAcceptableException(final NotAcceptableException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public NotAcceptableException unwrap() {
        return wrapped;
    }
}