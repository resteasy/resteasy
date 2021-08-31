package org.jboss.resteasy.client.exception;

import static org.jboss.resteasy.client.exception.WebApplicationExceptionWrapper.sanitize;

import java.util.Date;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;

/**
 * Wraps a {@link ServiceUnavailableException} with a {@linkplain #sanitize(Response) sanitized} response.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class ResteasyServiceUnavailableException extends ServiceUnavailableException implements WebApplicationExceptionWrapper<ServiceUnavailableException> {

   private static final long serialVersionUID = -4477873328299557209L;
   private final ServiceUnavailableException wrapped;

    ResteasyServiceUnavailableException(final ServiceUnavailableException wrapped) {
        super(wrapped.getMessage(), sanitize(wrapped.getResponse()), wrapped.getCause());
        this.wrapped = wrapped;
    }

    @Override
    public boolean hasRetryAfter() {
        return wrapped.hasRetryAfter();
    }

    @Override
    public Date getRetryTime(final Date requestTime) {
        return wrapped.getRetryTime(requestTime);
    }

    @Override
    public ServiceUnavailableException unwrap() {
        return wrapped;
    }
}
