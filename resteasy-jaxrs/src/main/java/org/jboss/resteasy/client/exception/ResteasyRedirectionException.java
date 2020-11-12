package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import java.net.URI;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.core.Response;

/**
 * Wraps a {@link RedirectionException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyRedirectionException extends RedirectionException implements WebApplicationExceptionWrapper<RedirectionException> {

    private final RedirectionException wrapped;

    ResteasyRedirectionException(final RedirectionException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()));
        this.wrapped = wrapped;
    }

    @Override
    public URI getLocation() {
        return wrapped.getLocation();
    }

    @Override
    public RedirectionException unwrap() {
        return wrapped;
    }
}
