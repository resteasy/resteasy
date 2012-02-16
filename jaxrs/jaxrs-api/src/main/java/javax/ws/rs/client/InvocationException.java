/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.client;

import javax.ws.rs.core.Response;

/**
 * A runtime exception thrown during the HTTP request invocation processing,
 * that signals a failure to process the HTTP request or response. The exception
 * message or nested {@link Throwable} cause SHOULD contain additional information
 * about the reason of the processing failure. The exception is also thrown when
 * the status code of the returned HTTP response indicates a response that is not
 * expected.
 * <p />
 * This exception is typically thrown by the HTTP invocation methods on an {@link Invocation}
 * or a {@link Client} instance. Additionally, the exception is also thrown by
 * {@link Response} {@code readEntity(...)} methods in case the returned response is
 * HTTP 204 (No Content).
 * <p />
 * Whenever the exception is thrown in the context of an existing {@link Response response},
 * the response will be {@link #getResponse() referenced} by the exception.
 *
 *
 * @author Marek Potociar
 * @since 2.0
 */
public class InvocationException extends ClientException {

    private static final long serialVersionUID = -8551966770517714263L;
    private transient final Response response;

    /**
     * Construct a client-side invocation exception without a response.
     * <p>
     * The client response wrapped by the exception will be set to {@code null}.
     *
     * @param message the message of the exception.
     */
    public InvocationException(final String message) {
        super(message);
        response = null;
    }

    /**
     * Constructs a new client-side invocation exception with the specified detail
     * message and cause. The nested {@link #getResponse() response reference}
     * will be set to {@code null}.
     * <p/>
     * Note that the detail message associated with {@code cause} is <i>not</i>
     * automatically incorporated in this runtime exception's detail message.
     *
     * @param  message the detail message (which is saved for later retrieval
     *     by the {@link #getMessage()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *     {@link #getCause()} method). (A {@code null} value is permitted,
     *     and indicates that the cause is nonexistent or unknown.)
     */
    public InvocationException(final String message, Throwable cause) {
        super(message, cause);
        response = null;
    }

    /**
     * Construct a client-side invocation exception and set the exception's
     * {@link #getResponse() response reference} to point to the provided
     * {@code response} input parameter. The message of the exception is set
     * to a value returned by {@code response.toString()}.
     * <p>
     * The entity of the referenced response will be {@link Response#bufferEntity() buffered}.
     *
     * @param response the client response. The message of the exception is set
     *     to a value returned by {@code response.toString()}.
     */
    public InvocationException(final Response response) {
        this(response, true);
    }

    /**
     * Construct a client-side invocation exception and set the exception's
     * {@link #getResponse() response reference} to point to the provided
     * {@code response} input parameter. The message of the exception is set
     * to a value returned by {@code response.toString()}.
     * <p>
     * The entity of the referenced response will be {@link Response#bufferEntity() buffered}
     * if the {@code bufferResponseEntity} is set to {@code true}.
     *
     * @param response the client response. The message of the exception is set
     *     to a value returned by {@code response.toString()}.
     * @param bufferResponseEntity if {@code true} the entity of the referenced
     *      response will be {@link Response#bufferEntity() buffered}.
     */
    public InvocationException(final Response response, final boolean bufferResponseEntity) {
        super(response.toString());
        if (bufferResponseEntity) {
            response.bufferEntity();
        }
        this.response = response;
    }

    /**
     * Construct a client-side invocation exception and set the exception's
     * {@link #getResponse() response reference} to point to the provided
     * {@code response} input parameter. The message of the exception is set
     * to a value of {@code message} input parameter.
     * <p>
     * The entity of the referenced response will be {@link Response#bufferEntity() buffered}.
     *
     * @param  message the detail message (which is saved for later retrieval
     *     by the {@link #getMessage()} method).
     * @param response the client response. The message of the exception is set
     *     to a value returned by {@code response.toString()}.
     */
    public InvocationException(final String message, final Response response) {
        this(message, response, true);
    }

    /**
     * Construct a client-side invocation exception and set the exception's
     * {@link #getResponse() response reference} to point to the provided
     * {@code response} input parameter. The message of the exception is set
     * to a value of {@code message} input parameter.
     * <p>
     * The entity of the referenced response will be {@link Response#bufferEntity() buffered}
     * if the {@code bufferResponseEntity} is set to {@code true}.
     *
     * @param  message the detail message (which is saved for later retrieval
     *     by the {@link #getMessage()} method).
     * @param response the client response. The message of the exception is set
     *     to a value returned by {@code response.toString()}.
     * @param bufferResponseEntity if {@code true} the entity of the referenced
     *      response will be {@link Response#bufferEntity() buffered}.
     */
    public InvocationException(final String message, final Response response,
            final boolean bufferResponseEntity) {
        super(message);
        if (bufferResponseEntity && response != null) {
            response.bufferEntity();
        }
        this.response = response;
    }

    /**
     * Get the client response associated with the exception. May return {@code null}
     * if the exception was not associated with any particular response.

     * @return the client response if set, otherwise {@code null}.
     */
    public Response getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "InvocationException{message=" + getMessage() + ", response=" + response + '}';
    }
}
