/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.core;

import java.util.concurrent.TimeUnit;

/**
 * A JAX-RS asynchronous response that provides access to asynchronous server side
 * response manipulation.
 * <p>
 * An instance of {@code AsynchronousResponse} is returned from a call to a {@code suspend(...)}
 * method on an injectable {@link ExecutionContext execution context} instance that is bound
 * to a currently processed request. The returned asynchronous response can be used to
 * asynchronously provide the request processing result or otherwise manipulate the suspended
 * client connection. The available operations include:
 * <ul>
 * <li>setting a default fall-back response (e.g. in case of a time-out event etc.)</li>
 * <li>resuming the request processing suspended either using this execution context instance
 * or via {@code @Suspend} annotation</li>
 * <li>cancel the suspended request processing</li>
 * </ul>
 * For an example usage of {@link ExecutionContext} and {@code AsynchronousResponse} kindly consult
 * the {@link javax.ws.rs.Suspend &#64;Suspend} annotation API documentation.
 * </p>
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @since 2.0
 */
public interface AsynchronousResponse {
    /**
     * Resume the suspended request processing using the provided response data.
     * <p>
     * The provided response data can be of any Java type that can be
     * returned from a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * The processing of the data by JAX-RS framework follows the same path as
     * it would for the response data returned synchronously by a JAX-RS resource
     * method.
     * The asynchronous response must be still in a {@link #isSuspended() suspended} state
     * for this method to succeed.
     * </p>
     *
     * @param response data to be sent back in response to the suspended request.
     * @throws IllegalStateException in case the response is not {@link #isSuspended() suspended}.
     * @see #resume(Throwable)
     */
    void resume(Object response) throws IllegalStateException;

    /**
     * Resume the suspended request processing using the provided throwable.
     *
     * <p>
     * For the provided throwable same rules apply as for an exception thrown
     * by a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * The processing of the throwable by JAX-RS framework follows the same path
     * as it would for any exception thrown by a JAX-RS resource method.
     * The asynchronous response must be still in a {@link #isSuspended() suspended} state
     * for this method to succeed.
     * </p>
     *
     * @param response an exception to be raised in response to the suspended
     *                 request.
     * @throws IllegalStateException in case the response is not {@link #isSuspended() suspended}.
     * @see #resume(Object)
     */
    void resume(Throwable response) throws IllegalStateException;

    /**
     * Set/update the suspend timeout.
     *
     * <p>
     * The new suspend timeout values override any timeout value specified either
     * programmatically via one of the {@code ExecutionContext.suspend(...)} methods or
     * {@link javax.ws.rs.Suspend declaratively}.
     * The asynchronous response must be still in a {@link #isSuspended() suspended} state
     * for this method to succeed.
     * </p>
     *
     * @param time suspend timeout value in the give time {@code unit}. Value lower
     *             or equal to 0 causes the context to suspend indefinitely.
     * @param unit suspend timeout value time unit.
     * @throws IllegalStateException in case the response is not {@link #isSuspended() suspended}.
     */
    void setSuspendTimeout(long time, TimeUnit unit) throws IllegalStateException;

    /**
     * Cancel the suspended request processing.
     *
     * This method causes that the underlying suspended client connection is closed eventually.
     * <p>
     * If a {@link #setFallbackResponse(Object) fallback response} was set, the fallback response
     * is returned to the client. In case no fallback response has been set,  JAX-RS implementations
     * should indicate that the request processing has been cancelled by sending back a
     * {@link Response.Status#INTERNAL_SERVER_ERROR HTTP 500} error response.
     * </p>
     * <p>
     * Invoking this method multiple times has the same effect as invoking it only once.
     * Invoking this method on an asynchronous response instance that has already been
     * resumed has no effect and the method call is ignored. Once the request is canceled,
     * any attempts to suspend or resume the execution context will result in an
     * {@link IllegalStateException} being thrown.
     * </p>
     */
    void cancel();

    /**
     * Check if the asynchronous response instance is in a suspended state.
     *
     * Method returns {@code true} if this asynchronous response is still suspended and has
     * not finished processing yet (either by resuming or canceling the response).
     *
     * @return {@code true} if this asynchronous response is in a suspend state, {@code false}
     *         otherwise.
     * @see #isCancelled()
     * @see #isDone()
     */
    boolean isSuspended();

    /**
     * Check if the asynchronous response instance has been cancelled.
     *
     * Method returns {@code true} if this asynchronous response has been canceled before
     * completion.
     *
     * @return {@code true} if this task was canceled before completion.
     * @see #isSuspended()
     * @see #isDone()
     */
    boolean isCancelled();

    /**
     * Check if the processing of a request this asynchronous response instance belongs to
     * has finished.
     *
     * Method returns {@code true} if the processing of a request this asynchronous response
     * is bound to is finished.
     * <p>
     * The request processing may be finished due to a normal termination, a suspend timeout, or
     * cancellation -- in all of these cases, this method will return {@code true}.
     * </p>
     *
     * @return {@code true} if this execution context has finished processing.
     * @see #isSuspended()
     * @see #isCancelled()
     */
    boolean isDone();

    /**
     * Set a default fall-back response to be used in case the suspended request
     * execution does not terminate normally via a call to {@code resume(...)} method
     * (e.g. is cancelled or times out).
     * <p>
     * The provided response data can be of any Java type that can be
     * returned from a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * If used, the processing of the data by JAX-RS framework follows the same
     * path as it would for the response data returned synchronously by a JAX-RS
     * resource method.
     * </p>
     *
     * @param response data to be sent back to the client in case the suspended
     *                 request is cancelled or times out.
     * @see #getFallbackResponse
     */
    void setFallbackResponse(Object response);

    /**
     * Get a default fall-back response to be send back to the client in case the
     * suspended request execution is cancelled or times out. The method may return {@code null}
     * if no default response was set in the execution context.
     *
     * @return default fall-back response to be sent back to the client in case the
     *         suspended request execution is cancelled or times out. Returns {@code null} if no default
     *         response was set.
     * @see #setFallbackResponse
     */
    Response getFallbackResponse();
}
