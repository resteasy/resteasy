/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.WebApplicationException;

/**
 * An injectable interface that provides access to asynchronous server side
 * request processing.
 * <p/>
 * The injected execution context instance is bound to the currently processed
 * request and can be used to
 * <ul>
 *   <li>suspend the request processing (with a defined timeout)</li>
 *   <li>set a default time-out response</li>
 *   <li>resume the request processing suspended either using this execution
 *       context instance or via {@link javax.ws.rs.Suspend @Suspend} annotation</li>
 *   <li>cancel the suspended request</li>
 * </ul>
 * TODO example.
 *
 * @author Marek Potociar
 * @see javax.ws.rs.Suspend @Suspend
 * @since 2.0
 */
public interface ExecutionContext {

    /**
     * Resume processing of the request bound to the execution context using
     * response data provided.
     * <p/>
     * The provided response data can be of any Java type that can be
     * returned from a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * The processing of the data by JAX-RS framework follows the same path as
     * it would for the response data returned synchronously by a JAX-RS resource
     * method.
     *
     * @param response data to be sent back in response to the suspended request.
     * @throws IllegalStateException in case the request has already been resumed
     *     or has been canceled previously.
     *
     * @see #resume(java.lang.Exception)
     */
    public void resume(Object response);

    /**
     * Resume processing of the request bound to the execution context using
     * an exception.
     * <p/>
     * For the provided exception same rules apply as for the exception thrown
     * by a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * The processing of the exception by JAX-RS framework follows the same path as
     * it would for the exception thrown by a JAX-RS resource method.
     *
     * @param response an exception to be raised in response to the suspended request.
     * @throws IllegalStateException in case the request has already been resumed
     *     or has been canceled previously.
     *
     * @see #resume(java.lang.Object)
     */
    public void resume(Exception response);

    /**
     * Programmatically suspend a request processing without explicitly specifying
     * any timeout. The method can only be invoked from within the context of
     * a running {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * Any response value returned from a resource method in which the request
     * processing has been suspended is ignored by the framework.
     * <p/>
     * The effective suspend timeout value is calculated using the following
     * mechanism:
     * <ol>
     *   <li>Set the effective timeout value to {@link javax.ws.rs.Suspend#NEVER}</li>
     *   <li>If there is a {@link javax.ws.rs.Suspend @Suspend} annotation on
     *       the enclosing JAX-RS resource method, the effective timeout value is
     *       updated to the value of {@link javax.ws.rs.Suspend#timeOut() @Suspend.timeOut}
     *       converted into milliseconds using the value of
     *       {@link javax.ws.rs.Suspend#timeUnit() @Suspend.timeUnit}</li>
     * </ol>
     * If the request processing is suspended with a positive timeout value, the
     * processing will be resumed once the specified timeout threshold is reached
     * provided the request processing was not explicitly resumed before the
     * suspending has expired. The request processing will be resumed using response
     * data returned by {@link #getResponse()} method. Should the {@code getResponse()}
     * return {@code null}, {@link WebApplicationException} is raised with a HTTP
     * 503 error status (Service unavailable). Use {@link #setResponse(java.lang.Object)}
     * method to customize the default timeout response.
     *
     * @return handle of the suspended request processing that can be used for
     *    querying its current state via one of the {@code Future.isXxx()} methods.
     *    Invoking any other method on the returned {@code Future} instance is
     *    not defined and reserved for future extensions of JAX-RS API.
     * @throws IllegalStateException in case the request has already been suspended,
     *     resumed or has been canceled previously.
     *
     * @see #suspend(long)
     * @see #suspend(long, java.util.concurrent.TimeUnit)
     * @see #setResponse(java.lang.Object)
     */
    public Future<?> suspend();

    /**
     * Programmatically suspend a request processing with explicitly specified
     * suspend timeout value in milliseconds. The method can only be invoked from
     * within the context of a running {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * Any response value returned from a resource method in which the request
     * processing has been suspended is ignored by the framework.
     * <p/>
     * The specified timeout value overrides {@link javax.ws.rs.Suspend#NEVER default
     * timeout value} as well as any timeout value specified {@link javax.ws.rs.Suspend
     * declaratively} for the enclosing resource method.
     * <p/>
     * If the request processing is suspended with a positive timeout value, the
     * processing will be resumed once the specified timeout threshold is reached
     * provided the request processing was not explicitly resumed before the
     * suspending has expired. The request processing will be resumed using response
     * data returned by {@link #getResponse()} method. Should the {@code getResponse()}
     * return {@code null}, {@link WebApplicationException} is raised with a HTTP
     * 503 error status (Service unavailable). Use {@link #setResponse(java.lang.Object)}
     * method to customize the default timeout response.
     * <p />
     * Note that in some concurrent scenarios a call to {@code resume(...)} may
     * occur before the call to {@code suspend(...)}. In which case the call to
     * {@code suspend(...)} is ignored. The returned {@link Future response future}
     * will be marked as {@link Future#isDone() done}.
     *
     * @param millis suspend timeout value in milliseconds.
     * @return handle of the suspended request processing that can be used for
     *    querying its current state via one of the {@code Future.isXxx()} methods.
     *    Invoking any other method on the returned {@code Future} instance is
     *    not defined and reserved for future extensions of JAX-RS API.
     * @throws IllegalStateException in case the request has already been suspended
     *     or has been canceled previously.
     *
     * @see #suspend()
     * @see #suspend(long, java.util.concurrent.TimeUnit)
     * @see #setResponse(java.lang.Object)
     */
    public Future<?> suspend(long millis);

    /**
     * Programmatically suspend a request processing with explicitly specified
     * suspend timeout value and its time unit. The method can only be invoked
     * from within the context of a running {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * Any response value returned from a resource method in which the request
     * processing has been suspended is ignored by the framework.
     * <p/>
     * The specified timeout value overrides {@link javax.ws.rs.Suspend#NEVER default
     * timeout value} as well as any timeout value specified {@link javax.ws.rs.Suspend
     * declaratively} for the enclosing resource method.
     * <p/>
     * If the request processing is suspended with a positive timeout value, the
     * processing will be resumed once the specified timeout threshold is reached
     * provided the request processing was not explicitly resumed before the
     * suspending has expired. The request processing will be resumed using response
     * data returned by {@link #getResponse()} method. Should the {@code getResponse()}
     * return {@code null}, {@link WebApplicationException} is raised with a HTTP
     * 503 error status (Service unavailable). Use {@link #setResponse(java.lang.Object)}
     * method to customize the default timeout response.
     * <p />
     * Note that in some concurrent scenarios a call to {@code resume(...)} may
     * occur before the call to {@code suspend(...)}. In which case the call to
     * {@code suspend(...)} is ignored. The returned {@link Future response future}
     * will be marked as {@link Future#isDone() done}.
     *
     * @param time suspend timeout value in the give time {@code unit}.
     * @param unit suspend timeout value time unit
     * @return handle of the suspended request processing that can be used for
     *    querying its current state via one of the {@code Future.isXxx()} methods.
     *    Invoking any other method on the returned {@code Future} instance is
     *    not defined and reserved for future extensions of JAX-RS API.
     * @throws IllegalStateException in case the request has already been suspended
     *     or has been canceled previously.
     *
     * @see #suspend()
     * @see #suspend(long, java.util.concurrent.TimeUnit)
     * @see #setResponse(java.lang.Object)
     */
    public Future<?> suspend(long time, TimeUnit unit);

    /**
     * Cancel the request processing.
     * <p/>
     * This method causes that the underlying network connection is closed without
     * any response being sent back to the client. Invoking this method multiple
     * times has the same effect as invoking it only once. Invoking this method
     * on a request that has already been resumed has no effect and the method
     * call is ignored.
     * <p/>
     * Once the request is canceled, any attempts to suspend or resume the execution
     * context will result in an {@link IllegalStateException} being thrown.
     */
    public void cancel();

    /**
     * Set the default response to be used in case the suspended request times out.
     * <p/>
     * The provided response data can be of any Java type that can be
     * returned from a {@link javax.ws.rs.HttpMethod JAX-RS resource method}.
     * If used, the processing of the data by JAX-RS framework follows the same
     * path as it would for the response data returned synchronously by a JAX-RS
     * resource method.
     *
     * @param response data to be sent back to the client in case the suspended
     *     request times out.
     * @see #getResponse()
     */
    public void setResponse(Object response);

    /**
     * Returns default response to be send back to the client in case the suspended
     * request times out. The method may return {@code null} if no default response
     * was set in the execution context.
     *
     * @return default response to be sent back to the client in case the suspended
     *     request times out or {@code null} if no default response was set.
     * @see #setResponse(java.lang.Object)
     */
    public Response getResponse();
}
