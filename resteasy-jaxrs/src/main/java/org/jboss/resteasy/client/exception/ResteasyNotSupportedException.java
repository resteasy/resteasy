package org.jboss.resteasy.client.exception;

import javax.ws.rs.core.Response;

/**
 * A runtime exception indicating a {@link javax.ws.rs.core.Response.Status#UNSUPPORTED_MEDIA_TYPE
 * bad client request}.
 *
 * @author Sergey Beryozkin
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyNotSupportedException extends ResteasyClientErrorException {

   private static final long serialVersionUID = -2237444986002030647L;

   /**
     * Construct a new bad client request exception.
     */
    public ResteasyNotSupportedException() {
        super(Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ResteasyNotSupportedException(final String message) {
        super(message, Response.Status.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotSupportedException(final Response response) {
        super(validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE));
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
    public ResteasyNotSupportedException(final String message, final Response response) {
        super(message, validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE));
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public ResteasyNotSupportedException(final Throwable cause) {
        super(Response.Status.UNSUPPORTED_MEDIA_TYPE, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public ResteasyNotSupportedException(final String message, final Throwable cause) {
        super(message, Response.Status.UNSUPPORTED_MEDIA_TYPE, cause);
    }

    /**
     * Construct a new bad client request exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 400}.
     */
    public ResteasyNotSupportedException(final Response response, final Throwable cause) {
        super(validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE), cause);
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
    public ResteasyNotSupportedException(final String message, final Response response, final Throwable cause) {
        super(message, validate(response, Response.Status.UNSUPPORTED_MEDIA_TYPE), cause);
    }
}
