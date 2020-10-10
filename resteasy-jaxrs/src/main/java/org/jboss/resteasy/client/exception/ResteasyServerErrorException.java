package org.jboss.resteasy.client.exception;

import javax.ws.rs.core.Response;

/**
 * A base runtime application exception indicating a server error
 * (HTTP {@code 5xx} status codes).
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyServerErrorException extends ResteasyWebApplicationException {

   private static final long serialVersionUID = -9133676047610191106L;

    /**
     * Construct a new server error exception.
     *
     * @param status server error status. Must be a {@code 5xx} status code.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code
     *                                  family.
     */
    public ResteasyServerErrorException(final Response.Status status) {
        super((Throwable) null, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  server error status. Must be a {@code 5xx} status code.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code
     *                                  family.
     */
    public ResteasyServerErrorException(final String message, final Response.Status status) {
        super(message, null, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param status server error status. Must be a {@code 5xx} status code.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR}
     *                                  status code family.
     */
    public ResteasyServerErrorException(final int status) {
        super((Throwable) null, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  server error status. Must be a {@code 5xx} status code.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR}
     *                                  status code family.
     */
    public ResteasyServerErrorException(final String message, final int status) {
        super(message, null, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param response server error response. Must have a status code set to a {@code 5xx}
     *                 status code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code family.
     */
    public ResteasyServerErrorException(final Response response) {
        super((Throwable) null, validate(response, Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response server error response. Must have a status code set to a {@code 5xx}
     *                 status code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code family.
     */
    public ResteasyServerErrorException(final String message, final Response response) {
        super(message, null, validate(response, Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param status server error status. Must be a {@code 5xx} status code.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code
     *                                  family.
     */
    public ResteasyServerErrorException(final Response.Status status, final Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  server error status. Must be a {@code 5xx} status code.
     * @param cause   the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code
     *                                  family.
     */
    public ResteasyServerErrorException(final String message, final Response.Status status, final Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param status server error status. Must be a {@code 5xx} status code.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR}
     *                                  status code family.
     */
    public ResteasyServerErrorException(final int status, final Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  server error status. Must be a {@code 5xx} status code.
     * @param cause   the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR}
     *                                  status code family.
     */
    public ResteasyServerErrorException(final String message, final int status, final Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param response server error response. Must have a status code set to a {@code 5xx}
     *                 status code.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code family.
     */
    public ResteasyServerErrorException(final Response response, final Throwable cause) {
        super(cause, validate(response, Response.Status.Family.SERVER_ERROR));
    }

    /**
     * Construct a new server error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response server error response. Must have a status code set to a {@code 5xx}
     *                 status code.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#SERVER_ERROR} status code family.
     */
    public ResteasyServerErrorException(final String message, final Response response, final Throwable cause) {
        super(message, cause, validate(response, Response.Status.Family.SERVER_ERROR));
    }
}
