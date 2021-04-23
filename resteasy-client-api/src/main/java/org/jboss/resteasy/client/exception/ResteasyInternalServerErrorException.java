package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link InternalServerErrorException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyInternalServerErrorException extends InternalServerErrorException implements WebApplicationExceptionWrapper<InternalServerErrorException> {

   private static final long serialVersionUID = 5293921582428847923L;
   private final InternalServerErrorException wrapped;

    ResteasyInternalServerErrorException(final InternalServerErrorException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public InternalServerErrorException unwrap() {
        return wrapped;
    }
}