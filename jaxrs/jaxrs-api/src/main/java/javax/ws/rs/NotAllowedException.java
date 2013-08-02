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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 * A runtime exception indicating a client requesting a resource method that is
 * {@link javax.ws.rs.core.Response.Status#METHOD_NOT_ALLOWED not allowed}.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class NotAllowedException extends ClientErrorException {

    private static final long serialVersionUID = -586776054369626119L;

    /**
     * Construct a new method not allowed exception.
     *
     * @param allowed     allowed request method.
     * @param moreAllowed more allowed request methods.
     * @throws NullPointerException in case the allowed method is {@code null}.
     */
    public NotAllowedException(String allowed, String... moreAllowed) {
        super(validateAllow(createNotAllowedResponse(allowed, moreAllowed)));
    }

    /**
     * Construct a new method not allowed exception.
     *
     * @param message     the detail message (which is saved for later retrieval
     *                    by the {@link #getMessage()} method).
     * @param allowed     allowed request method.
     * @param moreAllowed more allowed request methods.
     * @throws NullPointerException in case the allowed method is {@code null}.
     */
    public NotAllowedException(String message, String allowed, String... moreAllowed) {
        super(message, validateAllow(createNotAllowedResponse(allowed, moreAllowed)));
    }

    private static Response createNotAllowedResponse(String allowed, String... moreAllowed) {
        if (allowed == null) {
            throw new NullPointerException("No allowed method specified.");
        }
        Set<String> methods;
        if (moreAllowed != null && moreAllowed.length > 0) {
            methods = new HashSet<String>(moreAllowed.length + 1);
            methods.add(allowed);
            Collections.addAll(methods, moreAllowed);
        } else {
            methods = Collections.singleton(allowed);
        }

        return Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(methods).build();
    }

    /**
     * Construct a new method not allowed exception.
     * <p>
     * Note that this constructor does not validate the presence of HTTP
     * {@code Allow} header. I.e. it is possible
     * to use the constructor to create a client-side exception instance
     * even for an invalid HTTP {@code 405} response content returned from a server.
     * </p>
     *
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 405}.
     */
    public NotAllowedException(Response response) {
        super(validate(response, Response.Status.METHOD_NOT_ALLOWED));
    }

    /**
     * Construct a new method not allowed exception.
     * <p>
     * Note that this constructor does not validate the presence of HTTP
     * {@code Allow} header. I.e. it is possible
     * to use the constructor to create a client-side exception instance
     * even for an invalid HTTP {@code 405} response content returned from a server.
     * </p>
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 405}.
     */
    public NotAllowedException(String message, Response response) {
        super(message, validate(response, Response.Status.METHOD_NOT_ALLOWED));
    }

    /**
     * Construct a new method not allowed exception.
     *
     * @param cause          the underlying cause of the exception.
     * @param allowedMethods allowed request methods.
     * @throws IllegalArgumentException in case the allowed methods varargs are {@code null}.
     */
    public NotAllowedException(Throwable cause, String... allowedMethods) {
        super(validateAllow(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(allowedMethods).build()), cause);
    }

    /**
     * Construct a new method not allowed exception.
     *
     * @param message        the detail message (which is saved for later retrieval
     *                       by the {@link #getMessage()} method).
     * @param cause          the underlying cause of the exception.
     * @param allowedMethods allowed request methods.
     * @throws IllegalArgumentException in case the allowed methods varargs are {@code null}.
     */
    public NotAllowedException(String message, Throwable cause, String... allowedMethods) {
        super(message, validateAllow(Response.status(Response.Status.METHOD_NOT_ALLOWED).allow(allowedMethods).build()), cause);
    }

    /**
     * Construct a new method not allowed exception.
     *
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 405} or does not contain
     *                                  an HTTP {@code Allow} header.
     */
    public NotAllowedException(Response response, Throwable cause) {
        super(validateAllow(validate(response, Response.Status.METHOD_NOT_ALLOWED)), cause);
    }

    /**
     * Construct a new method not allowed exception.
     *
     * @param message  the detail message (which is saved for later retrieval
     *                 by the {@link #getMessage()} method).
     * @param response error response.
     * @param cause    the underlying cause of the exception.
     * @throws IllegalArgumentException in case the status code set in the response
     *                                  is not HTTP {@code 405} or does not contain
     *                                  an HTTP {@code Allow} header.
     */
    public NotAllowedException(String message, Response response, Throwable cause) {
        super(message, validateAllow(validate(response, Response.Status.METHOD_NOT_ALLOWED)), cause);
    }

    private static Response validateAllow(final Response response) {
        if (!response.getHeaders().containsKey(HttpHeaders.ALLOW)) {
            throw new IllegalArgumentException("Response does not contain required 'Allow' HTTP header.");
        }

        return response;
    }
}
