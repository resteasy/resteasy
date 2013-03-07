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

import java.net.URI;

import javax.ws.rs.core.Response;

/**
 * A runtime application exception indicating a request redirection
 * (HTTP {@code 3xx} status codes).
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class RedirectionException extends WebApplicationException {

    private static final long serialVersionUID = -2584325408291098012L;

    /**
     * Construct a new redirection exception.
     *
     * @param status   redirection status. Must be a {@code 3xx} redirection code.
     * @param location redirection URI placed into the response {@code Location} header.
     * @throws IllegalArgumentException in case the status code is {@code null} or is not from
     *                                  {@link javax.ws.rs.core.Response.Status.Family#REDIRECTION} status code
     *                                  family.
     */
    public RedirectionException(Response.Status status, URI location) {
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
    public RedirectionException(String message, Response.Status status, URI location) {
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
    public RedirectionException(int status, URI location) {
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
    public RedirectionException(String message, int status, URI location) {
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
    public RedirectionException(Response response) {
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
    public RedirectionException(String message, Response response) {
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
