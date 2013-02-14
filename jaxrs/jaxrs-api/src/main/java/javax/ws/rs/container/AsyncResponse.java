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
package javax.ws.rs.container;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * An injectable JAX-RS asynchronous response that provides means for asynchronous server side
 * response processing.
 * <p>
 * A new instance of {@code AsyncResponse} may be injected into a
 * {@link javax.ws.rs.HttpMethod resource or sub-resource method} parameter using
 * the {@link Suspended &#64;Suspend} annotation.
 * </p>
 * Each asynchronous response instance is bound to the running request and can be used to
 * asynchronously provide the request processing result or otherwise manipulate the suspended
 * client connection. The available operations include:
 * <ul>
 * <li>updating suspended state data (time-out value, response ...)</li>
 * <li>resuming the suspended request processing</li>
 * <li>canceling the suspended request processing</li>
 * </ul>
 * </p>
 * <p>
 * Following example demonstrates the use of the {@code AsyncResponse} for asynchronous
 * HTTP request processing:
 * </p>
 * <pre>
 * &#64;Path("/messages/next")
 * public class MessagingResource {
 *     private static final BlockingQueue&lt;AsyncResponse&gt; suspended =
 *             new ArrayBlockingQueue&lt;AsyncResponse&gt;(5);
 *
 *     &#64;GET
 *     public void readMessage(&#64;Suspended AsyncResponse ar) throws InterruptedException {
 *         suspended.put(ar);
 *     }
 *
 *     &#64;POST
 *     public String postMessage(final String message) throws InterruptedException {
 *         final AsyncResponse ar = suspended.take();
 *         ar.resume(message); // resumes the processing of one GET request
 *         return "Message sent";
 *     }
 * }
 * </pre>
 * <p>
 * If the asynchronous response was suspended with a positive timeout value, and has
 * not been explicitly resumed before the timeout has expired, the processing
 * will be resumed once the specified timeout threshold is reached, provided a positive
 * timeout value was set on the response.
 * </p>
 * <p>
 * By default a timed-out asynchronous response is resumed with a {@link javax.ws.rs.WebApplicationException}
 * that has {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE HTTP 503 (Service unavailable)}
 * error response status code set. This default behavior may be overridden by
 * {@link AsyncResponse#setTimeoutHandler(TimeoutHandler) setting} a custom {@link TimeoutHandler time-out handler}.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface AsyncResponse {
    /**
     * Constant specifying no suspend timeout value.
     */
    public static final long NO_TIMEOUT = 0;

    /**
     * Resume the suspended request processing using the provided response data.
     *
     * The provided response data can be of any Java type that can be
     * returned from a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * <p>
     * The asynchronous response must be still in a {@link #isSuspended() suspended} state
     * for this method to succeed.
     * </p>
     * <p>
     * By executing this method, the request is guaranteed to complete either successfully or
     * with an error. The data processing by the JAX-RS runtime follows the same path
     * as it would for the response data returned synchronously by a JAX-RS resource,
     * except that unmapped exceptions are not re-thrown by JAX-RS runtime to be handled by
     * a hosting I/O container. Instead, any unmapped exceptions are propagated to the hosting
     * I/O container via a container-specific callback mechanism. Depending on the container
     * implementation, propagated unmapped exceptions typically result in an error status
     * being sent to the client and/or the connection being closed.
     * </p>
     *
     * @param response data to be sent back in response to the suspended request.
     * @return {@code true} if the request processing has been resumed, returns {@code false} in case
     *         the request processing is not {@link #isSuspended() suspended} and could not be resumed.
     * @see #resume(Throwable)
     */
    public boolean resume(Object response);

    /**
     * Resume the suspended request processing using the provided throwable.
     *
     * For the provided throwable same rules apply as for an exception thrown
     * by a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * <p>
     * By executing this method, the request is guaranteed to complete either successfully or
     * with an error. The throwable processing by the JAX-RS runtime follows the same path
     * as it would for the response data returned synchronously by a JAX-RS resource,
     * except that unmapped exceptions are not re-thrown by JAX-RS runtime to be handled by
     * a hosting I/O container. Instead, any unmapped exceptions are propagated to the hosting
     * I/O container via a container-specific callback mechanism. Depending on the container
     * implementation, propagated unmapped exceptions typically result in an error status
     * being sent to the client and/or the connection being closed.
     * </p>
     *
     * @param response an exception to be raised in response to the suspended
     *                 request.
     * @return {@code true} if the response has been resumed, returns {@code false} in case
     *         the response is not {@link #isSuspended() suspended} and could not be resumed.
     * @see #resume(Object)
     */
    public boolean resume(Throwable response);

    /**
     * Cancel the suspended request processing.
     * <p>
     * When a request processing is cancelled using this method, the JAX-RS implementation
     * MUST indicate to the client that the request processing has been cancelled by sending
     * back a {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE HTTP 503 (Service unavailable)}
     * error response.
     * </p>
     * <p>
     * Invoking a {@code cancel(...)} method multiple times to cancel request processing has the same
     * effect as canceling the request processing only once. Invoking a {@code cancel(...)} method on
     * an asynchronous response instance that has already been cancelled or resumed has no effect and the
     * method call is ignored while returning {@code true}, in case the request has been cancelled previously.
     * Otherwise, in case the request has been resumed regularly (using a {@code resume(...) method}) or
     * resumed due to a time-out, method returns {@code false}.
     * </p>
     *
     * @return {@code true} if the request processing has been cancelled, returns {@code false} in case
     *         the request processing is not {@link #isSuspended() suspended} and could not be cancelled
     *         and is not {@link #isCancelled() cancelled} already.
     * @see #cancel(int)
     * @see #cancel(java.util.Date)
     */
    public boolean cancel();

    /**
     * Cancel the suspended request processing.
     * <p>
     * When a request processing is cancelled using this method, the JAX-RS implementation
     * MUST indicate to the client that the request processing has been cancelled by sending
     * back a {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE HTTP 503 (Service unavailable)}
     * error response with a {@code Retry-After} header set to the value provided by the method
     * parameter.
     * </p>
     * <p>
     * Invoking a {@code cancel(...)} method multiple times to cancel request processing has the same
     * effect as canceling the request processing only once. Invoking a {@code cancel(...)} method on
     * an asynchronous response instance that has already been cancelled or resumed has no effect and the
     * method call is ignored while returning {@code true}, in case the request has been cancelled previously.
     * Otherwise, in case the request has been resumed regularly (using a {@code resume(...) method}) or
     * resumed due to a time-out, method returns {@code false}.
     * </p>
     *
     * @param retryAfter a decimal integer number of seconds after the response is sent to the client that
     *                   indicates how long the service is expected to be unavailable to the requesting
     *                   client.
     * @return {@code true} if the request processing has been cancelled, returns {@code false} in case
     *         the request processing is not {@link #isSuspended() suspended} and could not be cancelled
     *         and is not {@link #isCancelled() cancelled} already.
     * @see #cancel
     * @see #cancel(java.util.Date)
     */
    public boolean cancel(int retryAfter);

    /**
     * Cancel the suspended request processing.
     * <p>
     * When a request processing is cancelled using this method, the JAX-RS implementation
     * MUST indicate to the client that the request processing has been cancelled by sending
     * back a {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE HTTP 503 (Service unavailable)}
     * error response with a {@code Retry-After} header set to the value provided by the method
     * parameter.
     * </p>
     * <p>
     * Invoking a {@code cancel(...)} method multiple times to cancel request processing has the same
     * effect as canceling the request processing only once. Invoking a {@code cancel(...)} method on
     * an asynchronous response instance that has already been cancelled or resumed has no effect and the
     * method call is ignored while returning {@code true}, in case the request has been cancelled previously.
     * Otherwise, in case the request has been resumed regularly (using a {@code resume(...) method}) or
     * resumed due to a time-out, method returns {@code false}.
     * </p>
     *
     * @param retryAfter a date that indicates how long the service is expected to be unavailable to the
     *                   requesting client.
     * @return {@code true} if the request processing has been cancelled, returns {@code false} in case
     *         the request processing is not {@link #isSuspended() suspended} and could not be cancelled
     *         and is not {@link #isCancelled() cancelled} already.
     * @see #cancel
     * @see #cancel(int)
     */
    public boolean cancel(Date retryAfter);

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
    public boolean isSuspended();

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
    public boolean isCancelled();

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
    public boolean isDone();

    /**
     * Set/update the suspend timeout.
     * <p>
     * The new suspend timeout values override any timeout value previously specified.
     * The asynchronous response must be still in a {@link #isSuspended() suspended} state
     * for this method to succeed.
     * </p>
     *
     * @param time suspend timeout value in the give time {@code unit}. Value lower
     *             or equal to 0 causes the context to suspend indefinitely.
     * @param unit suspend timeout value time unit.
     * @return {@code true} if the suspend time out has been set, returns {@code false} in case
     *         the request processing is not in the {@link #isSuspended() suspended} state.
     */
    public boolean setTimeout(long time, TimeUnit unit);

    /**
     * Set/replace a time-out handler for the suspended asynchronous response.
     * <p>
     * The time-out handler will be invoked when the suspend period of this
     * asynchronous response times out. The job of the time-out handler is to
     * resolve the time-out situation by either
     * <ul>
     * <li>resuming the suspended response</li>
     * <li>cancelling the suspended response</li>
     * <li>extending the suspend period by setting a new suspend time-out</li>
     * </ul>
     * </p>
     * <p>
     * Note that in case the response is suspended {@link #NO_TIMEOUT indefinitely},
     * the time-out handler may never be invoked.
     * </p>
     *
     * @param handler response time-out handler.
     */
    public void setTimeoutHandler(TimeoutHandler handler);

    /**
     * Register an asynchronous processing lifecycle callback class to receive lifecycle
     * events for the asynchronous response based on the implemented callback interfaces.
     *
     * @param callback callback class.
     * @return collection of registered callback interfaces. If the callback class does not
     *         implement any recognized callback interfaces, the returned collection will be
     *         empty.
     * @throws NullPointerException in case the callback class is {@code null}.
     */
    public Collection<Class<?>> register(Class<?> callback);

    /**
     * Register asynchronous processing lifecycle callback classes to receive lifecycle
     * events for the asynchronous response based on the implemented callback interfaces.
     *
     * @param callback  callback class.
     * @param callbacks additional callback classes.
     * @return map of registered classes and the callback interfaces registered for each class.
     *         If a callback class does not implement any recognized callback interfaces, the
     *         associated collection of registered interfaces for the class will be empty.
     * @throws NullPointerException in case any of the callback classes is {@code null}.
     */
    public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>... callbacks);

    /**
     * Register an asynchronous processing lifecycle callback instance to receive lifecycle
     * events for the asynchronous response based on the implemented callback interfaces.
     *
     * @param callback callback instance implementing one or more of the recognized callback
     *                 interfaces.
     * @return collection of registered callback interfaces. If the callback class does not
     *         implement any recognized callback interfaces, the returned collection will be
     *         empty.
     * @throws NullPointerException in case the callback instance is {@code null}.
     */
    public Collection<Class<?>> register(Object callback);

    /**
     * Register an asynchronous processing lifecycle callback instances to receive lifecycle
     * events for the asynchronous response based on the implemented callback interfaces.
     *
     * @param callback  callback instance.
     * @param callbacks additional callback instances.
     * @return map of registered classes and the callback interfaces registered for each class.
     *         If a callback class does not implement any recognized callback interfaces, the
     *         associated collection of registered interfaces for the class will be empty.
     * @throws NullPointerException in case any of the callback instances is {@code null}.
     */
    public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks);
}
