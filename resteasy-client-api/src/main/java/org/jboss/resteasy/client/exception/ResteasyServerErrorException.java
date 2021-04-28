package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link ServerErrorException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyServerErrorException extends ServerErrorException implements WebApplicationExceptionWrapper<ServerErrorException> {

   private static final long serialVersionUID = 8591476266091129117L;
   private final ServerErrorException wrapped;

    ResteasyServerErrorException(final ServerErrorException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public ServerErrorException unwrap() {
        return wrapped;
    }
}
