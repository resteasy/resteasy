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

import java.net.URI;

import javax.ws.rs.core.Response;

/**
 * A runtime application exception indicating a request redirection
 * (HTTP {@code 3xx} status codes).
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ResteasyRedirectionException extends ResteasyWebApplicationException {

   private static final long serialVersionUID = 1476906528845934753L;

    /**
     * Construct a new redirection exception.
     *
     * @param status   redirection status. Must be a {@code 3xx} redirection code.
     * @param location redirection URI placed into the response {@code Location} header.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION} status code
     *                                  family.
     */
    public ResteasyRedirectionException(final Response.Status status, final URI location) {
        super((Throwable) null, validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }

    /**
     * Construct a new redirection exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param status   redirection status. Must be a {@code 3xx} redirection code.
     * @param location redirection URI placed into the response {@code Location} header.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION} status code
     *                                  family.
     */
    public ResteasyRedirectionException(final String message, final Response.Status status,final  URI location) {
        super(message, null, validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }

    /**
     * Construct a new redirection exception.
     *
     * @param status   redirection status. Must be a {@code 3xx} redirection code.
     * @param location redirection URI placed into the response {@code Location} header.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION}
     *                                  status code family.
     */
    public ResteasyRedirectionException(final int status, final URI location) {
        super((Throwable) null, validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }

    /**
     * Construct a new redirection exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param status   redirection status. Must be a {@code 3xx} redirection code.
     * @param location redirection URI placed into the response {@code Location} header.
     * @throws IllegalArgumentException in case the status code is not a valid HTTP status code or
     *                                  if it is not from the {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION}
     *                                  status code family.
     */
    public ResteasyRedirectionException(final String message, final int status, final URI location) {
        super(message, null, validate(Response.status(status).location(location).build(), Response.Status.Family.REDIRECTION));
    }

    /**
     * Construct a new redirection exception.
     *
     * @param response redirection response. Must have a status code set to a {@code 3xx}
     *                 redirection code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION} status code family.
     */
    public ResteasyRedirectionException(final Response response) {
        super((Throwable) null, validate(response, Response.Status.Family.REDIRECTION));
    }

    /**
     * Construct a new redirection exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response redirection response. Must have a status code set to a {@code 3xx}
     *                 redirection code.
     * @throws IllegalArgumentException in case the response status code is not from the
     *                                  {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION} status code family.
     */
    public ResteasyRedirectionException(final String message, final Response response) {
        super(message, null, validate(response, Response.Status.Family.REDIRECTION));
    }

    /**
     * Get the redirection response location.
     *
     * @return redirection response location.
     */
    public URI getLocation() {
        return getResponse().getLocation();
    }
}
