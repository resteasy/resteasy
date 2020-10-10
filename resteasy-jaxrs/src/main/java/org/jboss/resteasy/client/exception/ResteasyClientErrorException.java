/*
 * Copyright (c) 2012, 2017 Oracle and/or its affiliates. All rights reserved.
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

import javax.ws.rs.core.Response;

/**
 * A base runtime application exception indicating a client request error
 * (HTTP {@code 4xx} status codes).
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyClientErrorException extends ResteasyWebApplicationException {

   private static final long serialVersionUID = -2611608930165969217L;

    /**
     * Construct a new client error exception.
     *
     * @param status client error status. Must be a {@code 4xx} status code.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code
     *                                  family.
     */
    public ResteasyClientErrorException(final Response.Status status) {
        super((Throwable) null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }


    /**
     * Construct a new client error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  client error status. Must be a {@code 4xx} status code.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code
     *                                  family.
     */
    public ResteasyClientErrorException(final String message, final Response.Status status) {
        super(message, null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param status client error status. Must be a {@code 4xx} status code.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR}
     *                                  status code family.
     */
    public ResteasyClientErrorException(final int status) {
        super((Throwable) null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  client error status. Must be a {@code 4xx} status code.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR}
     *                                  status code family.
     */
    public ResteasyClientErrorException(final String message, final int status) {
        super(message, null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param response client error response. Must have a status code set to a {@code 4xx}
     *                 status code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code family.
     */
    public ResteasyClientErrorException(final Response response) {
        super((Throwable) null, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response client error response. Must have a status code set to a {@code 4xx}
     *                 status code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code family.
     */
    public ResteasyClientErrorException(final String message, final Response response) {
        super(message, null, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param status client error status. Must be a {@code 4xx} status code.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code
     *                                  family.
     */
    public ResteasyClientErrorException(final Response.Status status, final Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  client error status. Must be a {@code 4xx} status code.
     * @param cause   the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code
     *                                  family.
     */
    public ResteasyClientErrorException(final String message, final Response.Status status, final Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param status client error status. Must be a {@code 4xx} status code.
     * @param cause  the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR}
     *                                  status code family.
     */
    public ResteasyClientErrorException(final int status, final Throwable cause) {
        super(cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param status  client error status. Must be a {@code 4xx} status code.
     * @param cause   the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR}
     *                                  status code family.
     */
    public ResteasyClientErrorException(final String message, final int status, final Throwable cause) {
        super(message, cause, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param response client error response. Must have a status code set to a {@code 4xx}
     *                 status code.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code family.
     */
    public ResteasyClientErrorException(final Response response, final Throwable cause) {
        super(cause, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    /**
     * Construct a new client error exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response client error response. Must have a status code set to a {@code 4xx}
     *                 status code.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code family.
     */
    public ResteasyClientErrorException(final String message, final Response response, final Throwable cause) {
        super(message, cause, validate(response, Response.Status.Family.CLIENT_ERROR));
    }
}
