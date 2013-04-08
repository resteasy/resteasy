/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs;

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
public class WebApplicationException extends RuntimeException {

    private static final long serialVersionUID = 8273970399584007146L;
    private final Response response;

    /**
     * Construct a new instance with a blank message and default HTTP status code of 500.
     */
    public WebApplicationException() {
        this((Throwable) null, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance with a blank message and default HTTP status code of 500.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @since 2.0
     */
    public WebApplicationException(String message) {
        this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
    }


    /**
     * Construct a new instance using the supplied response.
     *
     * @param response the response that will be returned to the client, a value
     *                 of null will be replaced with an internal server error response (status
     *                 code 500).
     */
    public WebApplicationException(final Response response) {
        this((Throwable) null, response);
    }

    /**
     * Construct a new instance using the supplied response.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response the response that will be returned to the client, a value
     *                 of null will be replaced with an internal server error response (status
     *                 code 500).
     * @since 2.0
     */
    public WebApplicationException(final String message, final Response response) {
        this(message, null, response);
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param status the HTTP status code that will be returned to the client.
     */
    public WebApplicationException(final int status) {
        this((Throwable) null, status);
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @since 2.0
     */
    public WebApplicationException(final String message, final int status) {
        this(message, null, status);
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public WebApplicationException(final Response.Status status) {
        this((Throwable) null, status);
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @throws IllegalArgumentException if status is {@code null}.
     * @since 2.0
     */
    public WebApplicationException(final String message, final Response.Status status) {
        this(message, null, status);
    }

    /**
     * Construct a new instance with a blank message and default HTTP status code of 500.
     *
     * @param cause the underlying cause of the exception.
     */
    public WebApplicationException(final Throwable cause) {
        this(cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance with a blank message and default HTTP status code of 500.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public WebApplicationException(final String message, final Throwable cause) {
        this(message, cause, Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Construct a new instance using the supplied response.
     *
     * @param response the response that will be returned to the client,
     *                 a value of null will be replaced with an internal server error
     *                 response (status code 500).
     * @param cause    the underlying cause of the exception.
     */
    public WebApplicationException(final Throwable cause, final Response response) {
        this(computeExceptionMessage(response), cause, response);
    }

    /**
     * Construct a new instance using the supplied response.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response the response that will be returned to the client,
     *                 a value of null will be replaced with an internal server error
     *                 response (status code 500).
     * @param cause    the underlying cause of the exception.
     * @since 2.0
     */
    public WebApplicationException(final String message, final Throwable cause, final Response response) {
        super(message, cause);
        if (response == null) {
            this.response = Response.serverError().build();
        } else {
            this.response = response;
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
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @param cause  the underlying cause of the exception.
     */
    public WebApplicationException(final Throwable cause, final int status) {
        this(cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public WebApplicationException(final String message, final Throwable cause, final int status) {
        this(message, cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param status the HTTP status code that will be returned to the client.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException if status is {@code null}.
     */
    public WebApplicationException(final Throwable cause, final Response.Status status)
            throws IllegalArgumentException {
        this(cause, Response.status(status).build());
    }

    /**
     * Construct a new instance with a blank message and specified HTTP status code.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  the HTTP status code that will be returned to the client.
     * @param cause   the underlying cause of the exception.
     * @since 2.0
     */
    public WebApplicationException(final String message, final Throwable cause, final Response.Status status)
            throws IllegalArgumentException {
        this(message, cause, Response.status(status).build());
    }

    /**
     * Get the HTTP response.
     *
     * @return the HTTP response.
     */
    public Response getResponse() {
        return response;
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
}
