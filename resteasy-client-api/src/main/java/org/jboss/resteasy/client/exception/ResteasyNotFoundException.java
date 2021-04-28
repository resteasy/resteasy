package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link NotFoundException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyNotFoundException extends NotFoundException implements WebApplicationExceptionWrapper<NotFoundException> {

   private static final long serialVersionUID = 8915809730318765630L;
   private final NotFoundException wrapped;

    ResteasyNotFoundException(final NotFoundException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public NotFoundException unwrap() {
        return wrapped;
    }
}
