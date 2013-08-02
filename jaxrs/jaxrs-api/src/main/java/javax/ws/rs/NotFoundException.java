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
 * A runtime exception indicating a resource requested by a client was
 * {@link javax.ws.rs.core.Response.Status#NOT_FOUND not found} on the server.
 *
 * @author Sergey Beryozkin
 * @author Marek Potociar
 * @since 2.0
 */
public class NotFoundException extends ClientErrorException {

    private static final long serialVersionUID = -6820866117511628388L;

    /**
     * Construct a new "not found" exception.
     */
    public NotFoundException() {
        super(Response.Status.NOT_FOUND);
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     */
    public NotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 404}.
     */
    public NotFoundException(Response response) {
        super(validate(response, Response.Status.NOT_FOUND));
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 404}.
     */
    public NotFoundException(String message, Response response) {
        super(message, validate(response, Response.Status.NOT_FOUND));
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param cause the underlying cause of the exception.
     */
    public NotFoundException(Throwable cause) {
        super(Response.Status.NOT_FOUND, cause);
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the underlying cause of the exception.
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, Response.Status.NOT_FOUND, cause);
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 404}.
     */
    public NotFoundException(Response response, Throwable cause) {
        super(validate(response, Response.Status.NOT_FOUND), cause);
    }

    /**
     * Construct a new "not found" exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 404}.
     */
    public NotFoundException(String message, Response response, Throwable cause) {
        super(message, validate(response, Response.Status.NOT_FOUND), cause);
    }
}
