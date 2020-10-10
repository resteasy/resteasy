package org.jboss.resteasy.client.exception;

import javax.ws.rs.core.Response;

/**
 * A runtime exception indicating an {@link javax.ws.rs.core.Response.Status#INTERNAL_SERVER_ERROR
 * internal server error}.
 *
 * @author Sergey Beryozkin
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyInternalServerErrorException extends ResteasyServerErrorException {

   private static final long serialVersionUID = 7044351593817586942L;

    /**
     * Construct a new internal server error exception.
     */
    public ResteasyInternalServerErrorException() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public ResteasyInternalServerErrorException(final String message) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param response internal server error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 500}.
     */
    public ResteasyInternalServerErrorException(final Response response) {
        super(validate(response, Response.Status.INTERNAL_SERVER_ERROR));
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response internal server error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 500}.
     */
    public ResteasyInternalServerErrorException(final String message, final Response response) {
        super(message, validate(response, Response.Status.INTERNAL_SERVER_ERROR));
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public ResteasyInternalServerErrorException(final Throwable cause) {
        super(Response.Status.INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public ResteasyInternalServerErrorException(final String message, final Throwable cause) {
        super(message, Response.Status.INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param response internal server error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 500}.
     */
    public ResteasyInternalServerErrorException(final Response response, final Throwable cause) {
        super(validate(response, Response.Status.INTERNAL_SERVER_ERROR), cause);
    }

    /**
     * Construct a new internal server error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response internal server error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 500}.
     */
    public ResteasyInternalServerErrorException(final String message, final Response response, final Throwable cause) {
        super(message, validate(response, Response.Status.INTERNAL_SERVER_ERROR), cause);
    }
}
