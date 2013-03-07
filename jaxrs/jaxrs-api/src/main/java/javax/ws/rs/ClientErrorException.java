/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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
 * A base runtime application exception indicating a client request error
 * (HTTP {@code 4xx} status codes).
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class ClientErrorException extends WebApplicationException {

    private static final long serialVersionUID = -4101970664444907990L;

    /**
     * Construct a new client error exception.
     *
     * @param status client error status. Must be a {@code 4xx} status code.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#CLIENT_ERROR} status code
     *                                  family.
     */
    public ClientErrorException(Response.Status status) {
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
    public ClientErrorException(String message, Response.Status status) {
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
    public ClientErrorException(int status) {
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
    public ClientErrorException(String message, int status) {
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
    public ClientErrorException(Response response) {
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
    public ClientErrorException(String message, Response response) {
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
    public ClientErrorException(Response.Status status, Throwable cause) {
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
    public ClientErrorException(String message, Response.Status status, Throwable cause) {
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
    public ClientErrorException(int status, Throwable cause) {
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
    public ClientErrorException(String message, int status, Throwable cause) {
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
    public ClientErrorException(Response response, Throwable cause) {
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
    public ClientErrorException(String message, Response response, Throwable cause) {
        super(message, cause, validate(response, Response.Status.Family.CLIENT_ERROR));
    }
}
