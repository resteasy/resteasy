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

import java.util.concurrent.TimeUnit;

/**
 * An injectable interface that provides access to asynchronous server side
 * request processing.
 * <p>
 * The injected execution context instance is bound to the currently processed
 * request and can be used to programmatically suspend the request processing
 * (with a defined timeout).
 * </p>
 * <p>
 * For an example usage of {@code ExecutionContext} kindly consult the
 * {@link javax.ws.rs.Suspend &#64;Suspend} annotation API documentation.
 * </p>
 *
 * @author Marek Potociar
 * @see javax.ws.rs.Suspend &#64;Suspend
 * @since 2.0
 */
public interface ExecutionContext {

    /**
     * Programmatically suspend a request processing without explicitly specifying
     * any timeout.
     * <p>
     * The method can only be invoked from within the context of a running
     * {@link javax.ws.rs.HttpMethod JAX-RS resource method} that has not been
     * previously suspended either programmatically using one of the {@code suspend(...)}
     * methods on this execution context instance or declaratively by placing a
     * {@link javax.ws.rs.Suspend &#64;Suspend} annotation on the JAX-RS resource or
     * sub-resource method associated with the current request processing execution
     * context.
     * </p>
     * <p>
     * While the {@link AsynchronousResponse asynchronous response} returned from this method
     * is still suspended, the suspend timeout value may be updated using the
     * {@link AsynchronousResponse#setSuspendTimeout(long, TimeUnit)} method.
     * </p>
     * <p>
     * Any response value returned from the resource method in which the request
     * processing has been suspended is ignored by the framework.
     * </p>
     *
     * @return reference to a suspended asynchronous response.
     * @throws IllegalStateException in case the request has already been previously suspended.
     * @see #suspend(long)
     * @see #suspend(long, java.util.concurrent.TimeUnit)
     */
    public AsynchronousResponse suspend() throws IllegalStateException;

    /**
     * Programmatically suspend a request processing explicitly specifying a suspend
     * timeout value in milliseconds.
     * <p>
     * The method can only be invoked from within the context of a running
     * {@link javax.ws.rs.HttpMethod JAX-RS resource method} that has not been
     * previously suspended either programmatically using one of the {@code suspend(...)}
     * methods on this execution context instance or declaratively by placing a
     * {@link javax.ws.rs.Suspend &#64;Suspend} annotation on the JAX-RS resource or
     * sub-resource method associated with the current request processing execution
     * context.
     * </p>
     * <p>
     * While the {@link AsynchronousResponse asynchronous response} returned from this method
     * is still suspended, the suspend timeout value may be updated using the
     * {@link AsynchronousResponse#setSuspendTimeout(long, TimeUnit)} method.
     * </p>
     * <p>
     * Any response value returned from the resource method in which the request
     * processing has been suspended is ignored by the framework.
     * </p>
     *
     * @param millis suspend timeout value in milliseconds. Value lower
     *               or equal to 0 causes the context to suspend indefinitely.
     * @return reference to a suspended asynchronous response.
     * @throws IllegalStateException in case the request has already been previously suspended.
     * @see #suspend()
     * @see #suspend(long, java.util.concurrent.TimeUnit)
     */
    public AsynchronousResponse suspend(long millis) throws IllegalStateException;

    /**
     * Programmatically suspend a request processing explicitly specifying a suspend
     * timeout value in a given time unit.
     * <p>
     * The method can only be invoked from within the context of a running
     * {@link javax.ws.rs.HttpMethod JAX-RS resource method} that has not been
     * previously suspended either programmatically using one of the {@code suspend(...)}
     * methods on this execution context instance or declaratively by placing a
     * {@link javax.ws.rs.Suspend &#64;Suspend} annotation on the JAX-RS resource or
     * sub-resource method associated with the current request processing execution
     * context.
     * </p>
     * <p>
     * While the {@link AsynchronousResponse asynchronous response} returned from this method
     * is still suspended, the suspend timeout value may be updated using the
     * {@link AsynchronousResponse#setSuspendTimeout(long, TimeUnit)} method.
     * </p>
     * <p>
     * Any response value returned from the resource method in which the request
     * processing has been suspended is ignored by the framework.
     * </p>
     *
     * @param time suspend timeout value in the give time {@code unit}. Value lower
     *             or equal to 0 causes the context to suspend indefinitely.
     * @param unit suspend timeout value time unit
     * @return reference to a suspended asynchronous response.
     * @throws IllegalStateException in case the request has already been previously suspended.
     * @see #suspend()
     * @see #suspend(long)
     */
    public AsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException;
}
