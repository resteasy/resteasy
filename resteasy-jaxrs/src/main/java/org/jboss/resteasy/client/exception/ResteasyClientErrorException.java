package org.jboss.resteasy.client.exception;


import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.Response;

/**
 * Wraps a {@link ClientErrorException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyClientErrorException extends ClientErrorException implements WebApplicationExceptionWrapper<ClientErrorException> {

    private final ClientErrorException wrapped;

    ResteasyClientErrorException(final ClientErrorException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public ClientErrorException unwrap() {
        return wrapped;
    }
}
