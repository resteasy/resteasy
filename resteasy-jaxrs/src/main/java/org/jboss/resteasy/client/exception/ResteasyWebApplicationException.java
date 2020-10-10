/*
 * Copyright (c) 2010, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jboss.resteasy.client.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Runtime exception for applications.
 * <p>
 * This exception may be thrown by a resource method, provider or
 * {@link javax.ws.rs.core.StreamingOutput} implementation if a specific
 * HTTP error response needs to be produced. Only effective if thrown prior to
 * the response being committed.
 *
 * @author Paul Sandoz
 * @author Marek Potociar
 * @since 1.0
 */
public class ResteasyWebApplicationException extends WebApplicationException {

    private static final long serialVersionUID = -3726042013855258887L;
    private static final Response dummyResponse = Response.ok("dummy").build();
    private Response originalResponse;

    /**
     * Construct a new instance with a default HTTP status code of 500
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     */
    public ResteasyWebApplicationException() {//ok
        this((Throwable) null, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance with the supplied message and a default HTTP status code of 500.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message) {
        this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
    }


    /**
     * Construct a new instance using the supplied response
     * and a default message generated from the response's HTTP status code and the associated HTTP status reason phrase.
     *
     * @param response the response that will be returned to the client, a value
     *                 of null will be replaced with an internal server error response (status
     *                 code 500).
     */
    public ResteasyWebApplicationException(final Response response) {
        this((Throwable) null, response);
    }

    /**
     * Construct a new instance using the supplied message and response.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response the response that will be returned to the client, a value
     *                 of null will be replaced with an internal server error response (status
     *                 code 500).
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Response response) {
        this(message, null, response);
    }

    /**
     * Construct a new instance with the supplied HTTP status code
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     *
     * @param status the HTTP status code that will be returned to the client.
     */
    public ResteasyWebApplicationException(final int status) {
        this((Throwable) null, status);
    }

    /**
     * Construct a new instance with a supplied message and HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final int status) {
        this(message, null, status);
    }

    /**
     * Construct a new instance with the supplied HTTP status
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public ResteasyWebApplicationException(final Response.Status status) {
        this((Throwable) null, status);
    }

    /**
     * Construct a new instance with the supplied message and HTTP status.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @throws IllegalArgumentException if status is {@code null}.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Response.Status status) {
        this(message, null, status);
    }

    /**
     * Construct a new instance with the supplied root cause, default HTTP status code of 500
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     *
     * @param cause the underlying cause of the exception.
     */
    public ResteasyWebApplicationException(final Throwable cause) {
        this(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance with the supplied message, root cause and default HTTP status code of 500.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Throwable cause) {
        this(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance with the supplied root cause, response
     * and a default message generated from the response's HTTP status code and the associated HTTP status reason phrase.
     *
     * @param response the response that will be returned to the client,
     *                 a value of null will be replaced with an internal server error
     *                 response (status code 500).
     * @param cause    the underlying cause of the exception.
     */
    public ResteasyWebApplicationException(final Throwable cause, final Response response) {
        this(computeExceptionMessage(response), cause, response);
    }

    /**
     * Construct a new instance with the supplied message, root cause and response.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response the response that will be returned to the client,
     *                 a value of null will be replaced with an internal server error
     *                 response (status code 500).
     * @param cause    the underlying cause of the exception.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Throwable cause, final Response response) {
        super(message, cause, dummyResponse);
        if (response == null) {
            this.originalResponse = Response.serverError().build();
        } else {
            this.originalResponse = response;
        }
    }

    private static String computeExceptionMessage(Response response) {
        final Response.StatusType statusInfo;
        if (response != null) {
            statusInfo = response.getStatusInfo();
        } else {
            statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
    }

    /**
     * Construct a new instance with the supplied root cause, HTTP status code
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @param cause  the underlying cause of the exception.
     */
    public ResteasyWebApplicationException(final Throwable cause, final int status) {
        this(cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with the supplied message, root cause and HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Throwable cause, final int status) {
        this(message, cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with the supplied root cause, HTTP status code
     * and a default message generated from the HTTP status code and the associated HTTP status reason phrase.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public ResteasyWebApplicationException(final Throwable cause, final Response.Status status)
            throws IllegalArgumentException {
        this(cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with a the supplied message, root cause and HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public ResteasyWebApplicationException(final String message, final Throwable cause, final Response.Status status)
            throws IllegalArgumentException {
        this(message, cause, Response.status(status).build());
    }

    /**
     * Get the HTTP response.
     *
     * @return the HTTP response.
     */
    @Override
    public Response getResponse() {
        return null;
    }

    /**
     * Validate that a {@link javax.ws.rs.core.Response} object has an expected HTTP response
     * status code set.
     *
     * @param response       response object.
     * @param expectedStatus expected response status code.
     * @return validated response object.
     * @throws IllegalArgumentException if the response validation failed.
     * @since 2.0
     */
    static Response validate(final Response response, Response.Status expectedStatus) {
        if (expectedStatus.getStatusCode() != response.getStatus()) {
            throw new IllegalArgumentException(String.format("Invalid response status code. Expected [%d], was [%d].",
                    expectedStatus.getStatusCode(), response.getStatus()));
        }
        return response;
    }

    /**
     * Validate that a {@link javax.ws.rs.core.Response} object has an expected HTTP response
     * status code set.
     *
     * @param response             response object.
     * @param expectedStatusFamily expected response status code family.
     * @return validated response object.
     * @throws IllegalArgumentException if the response validation failed.
     * @since 2.0
     */
    static Response validate(final Response response, Response.Status.Family expectedStatusFamily) {
        if (response.getStatusInfo().getFamily() != expectedStatusFamily) {
            throw new IllegalArgumentException(String.format(
                    "Status code of the supplied response [%d] is not from the required status code family \"%s\".",
                    response.getStatus(), expectedStatusFamily));
        }
        return response;
    }

   public Response getOriginalResponse()
   {
      return originalResponse;
   }
}
