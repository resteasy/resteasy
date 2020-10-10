package org.jboss.resteasy.client.exception;

import java.util.Date;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;
import static javax.ws.rs.core.HttpHeaders.RETRY_AFTER;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

/**
 * A runtime exception indicating that the requested resource
 * {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE cannot be served}.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyServiceUnavailableException extends ResteasyServerErrorException {

   private static final long serialVersionUID = 1452508636757673105L;

   /**
     * Construct a new "service unavailable" exception without any "Retry-After" information
     * specified for the failed request.
     */
    public ResteasyServiceUnavailableException() {
        super(Response.status(SERVICE_UNAVAILABLE).build());
    }

    /**
     * Construct a new "service unavailable" exception without any "Retry-After" information
     * specified for the failed request.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ResteasyServiceUnavailableException(final String message) {
        super(message, Response.status(SERVICE_UNAVAILABLE).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     */
    public ResteasyServiceUnavailableException(final Long retryAfter) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     */
    public ResteasyServiceUnavailableException(final String message, final Long retryAfter) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param retryAfter a date/time after which the failed request may be retried.
     */
    public ResteasyServiceUnavailableException(final Date retryAfter) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter a date/time after which the failed request may be retried.
     */
    public ResteasyServiceUnavailableException(final String message, final Date retryAfter) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build());
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ResteasyServiceUnavailableException(final Response response) {
        super(validate(response, SERVICE_UNAVAILABLE));
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ResteasyServiceUnavailableException(final String message, final Response response) {
        super(message, validate(response, SERVICE_UNAVAILABLE));
    }

    /**
     * Construct a new "service unavailable" exception with a date specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param retryAfter a date/time after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ResteasyServiceUnavailableException(final Date retryAfter, final Throwable cause) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with a date specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter a date/time after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ResteasyServiceUnavailableException(final String message, final Date retryAfter, final Throwable cause) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ResteasyServiceUnavailableException(final Long retryAfter, final Throwable cause) {
        super(Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception with an interval specifying
     * the "Retry-After" information for the failed request and an underlying
     * request failure cause.
     *
     * @param message    the detail message (which is saved for later retrieval
     *                   by the {@link #getMessage()} method).
     * @param retryAfter decimal interval in seconds after which the failed request may be retried.
     * @param cause      the underlying cause of the exception.
     */
    public ResteasyServiceUnavailableException(final String message, final Long retryAfter, final Throwable cause) {
        super(message, Response.status(SERVICE_UNAVAILABLE).header(RETRY_AFTER, retryAfter).build(), cause);
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ResteasyServiceUnavailableException(final Response response, final Throwable cause) {
        super(validate(response, SERVICE_UNAVAILABLE), cause);
    }

    /**
     * Construct a new "service unavailable" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 503}.
     */
    public ResteasyServiceUnavailableException(final String message, final Response response, final Throwable cause) {
        super(message, validate(response, SERVICE_UNAVAILABLE), cause);
    }

    /**
     * Check if the underlying response contains the information on when is it
     * possible to {@link HttpHeaders#RETRY_AFTER retry the request}.
     *
     * @return {@code true} in case the retry time is specified in the underlying
     *         response, {@code false} otherwise.
     */
    public boolean hasRetryAfter() {
        return getResponse().getHeaders().containsKey(RETRY_AFTER);
    }

    /**
     * Get the retry time for the failed request.
     *
     * @param requestTime time of sending the original request that may be used to compute
     *                    the retry time (in case the retry time information specified as
     *                    a decimal interval in seconds).
     * @return time when the request may be retried or {@code null} if there is no retry
     *         information available.
     * @throws NullPointerException in case the {@code requestTime} parameter is {@code null}.
     */
    public Date getRetryTime(final Date requestTime) {
        final String value = getResponse().getHeaderString(RETRY_AFTER);
        if (value == null) {
            return null;
        }

        try {
            Long interval = Long.parseLong(value);
            return new Date(requestTime.getTime() + interval * 1000);
        } catch (NumberFormatException ex) {
            // not an decimal value; ignoring exception and parsing as date
        }

        final RuntimeDelegate.HeaderDelegate<Date> dateDelegate =
                RuntimeDelegate.getInstance().createHeaderDelegate(Date.class);
        return dateDelegate.fromString(value);
    }
}
