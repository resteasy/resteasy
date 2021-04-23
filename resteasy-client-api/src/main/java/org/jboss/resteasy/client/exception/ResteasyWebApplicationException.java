package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link WebApplicationException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyWebApplicationException extends WebApplicationException implements WebApplicationExceptionWrapper<WebApplicationException> {

   private static final long serialVersionUID = -8805699073882024461L;
   private final WebApplicationException wrapped;

    /**
     * Creates a new exception based on the wrapped exception. The response will be
     * {@linkplain #sanitize(Response) sanitized}.
     *
     * @param wrapped the exception to wrap
     */
    public ResteasyWebApplicationException(final WebApplicationException wrapped) {
        super(wrapped.getMessage(), wrapped.getCause(), sanitize(wrapped.getResponse()));
        this.wrapped = wrapped;
    }

    @Override
    public WebApplicationException unwrap() {
        return wrapped;
    }
}