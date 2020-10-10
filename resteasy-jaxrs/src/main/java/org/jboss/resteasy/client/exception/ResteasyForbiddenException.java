package org.jboss.resteasy.client.exception;

import javax.ws.rs.core.Response;

/**
 * A runtime exception indicating a {@link javax.ws.rs.core.Response.Status#FORBIDDEN
 * bad client request}.
 *
 * @author Sergey Beryozkin
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyForbiddenException extends ResteasyClientErrorException {

   private static final long serialVersionUID = 6424530481771647593L;

   /**
     * Construct a new bad client request exception.
     */
    public ResteasyForbiddenException() {
        super(Response.Status.FORBIDDEN);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ResteasyForbiddenException(final String message) {
        super(message, Response.Status.FORBIDDEN);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyForbiddenException(final Response response) {
        super(validate(response, Response.Status.FORBIDDEN));
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyForbiddenException(final String message, final Response response) {
        super(message, validate(response, Response.Status.FORBIDDEN));
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public ResteasyForbiddenException(final Throwable cause) {
        super(Response.Status.FORBIDDEN, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public ResteasyForbiddenException(final String message, final Throwable cause) {
        super(message, Response.Status.FORBIDDEN, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyForbiddenException(final Response response, final Throwable cause) {
        super(validate(response, Response.Status.FORBIDDEN), cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyForbiddenException(final String message, final Response response, final Throwable cause) {
        super(message, validate(response, Response.Status.FORBIDDEN), cause);
    }
}
