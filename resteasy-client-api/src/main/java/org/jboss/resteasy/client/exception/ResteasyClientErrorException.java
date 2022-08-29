package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link ClientErrorException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyClientErrorException extends ClientErrorException implements WebApplicationExceptionWrapper<ClientErrorException> {

   private static final long serialVersionUID = 6839671465938091898L;
   private final ClientErrorException wrapped;
   private final Response sanitizedResponse;

    ResteasyClientErrorException(final ClientErrorException wrapped) {
        super(wrapped.getMessage(), wrapped.getResponse(), wrapped.getCause());
        this.wrapped = wrapped;
        this.sanitizedResponse = sanitize(wrapped.getResponse());
    }

    @Override
    public ClientErrorException unwrap() {
        return wrapped;
    }

    @Override
    public Response getSanitizedResponse() {
        return sanitizedResponse;
    }
}