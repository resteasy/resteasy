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
package javax.ws.rs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Automatically suspend the request processing {@link javax.ws.rs.core.ExecutionContext
 * executioncontext} for the invoked JAX-RS {@link HttpMethod resource or sub-resource
 * method}. The annotation is ignored if it is used on any method other than JAX-RS resource
 * or sub-resource method.
 * <p>
 * A suspended request processing can be programmatically suspended or resumed using an injectable
 * {@link javax.ws.rs.core.ExecutionContext} instance bound to request being processed:
 * </p>
 *
 * <pre>
 * &#64;Path("/messages/next")
 * public class SimpleAsyncEventResource {
 *     private static final BlockingQueue&lt;AsynchronousResponse&gt; suspended =
 *             new ArrayBlockingQueue&lt;AsynchronousResponse&gt;(5);
 *     &#64;Context ExecutionContext ctx;
 *
 *     &#64;GET
 *     public void pickUpMessage() throws InterruptedException {
 *         final AsynchronousResponse ar = ctx.suspend();
 *         suspended.put(ar);
 *     }
 *
 *     &#64;POST
 *     public String postMessage(final String message) throws InterruptedException {
 *         final AsynchronousResponse ar = suspended.take();
 *         ar.resume(message); // resumes the processing of one GET request
 *         return "Message sent";
 *     }
 * }
 * </pre>
 *
 * <p>
 * The same functionality as in the example above can be also achieved by placing
 * {@code @Suspend} annotation on a resource method (an equivalent to calling
 * {@link javax.ws.rs.core.ExecutionContext#suspend()} as the first step
 * upon entering the method). This also means that any subsequent programmatic
 * invocation of a {@code ExecutionContext.suspend(...)} methods would be illegal in
 * the context of a method suspended via {@code @Suspend} annotation.
 * The {@link javax.ws.rs.core.AsynchronousResponse} instance associated with the
 * suspended request processing can be injected into the suspended method as one of
 * it's parameters. Following example illustrates the use of {@code @Suspend} annotation
 * equivalent to the example using the {@code ExecutionContext} above:
 * </p>
 *
 * <pre>
 * &#64;Path("/messages/next")
 * public class SimpleAsyncEventResource {
 *     private static final BlockingQueue&lt;AsynchronousResponse&gt; suspended =
 *             new ArrayBlockingQueue&lt;AsynchronousResponse&gt;(5);
 *
 *     &#64;GET
 *     &#64;Suspend
 *     public void pickUpMessage(AsynchronousResponse ar) throws InterruptedException {
 *         suspended.put(ar);
 *     }
 *
 *     &#64;POST
 *     public String postMessage(final String message) throws InterruptedException {
 *         final AsynchronousResponse ar = suspended.take();
 *         ar.resume(message); // resume the processing of one GET request
 *         return "Message sent";
 *     }
 * }
 * </pre>
 *
 * <p>
 * Typically, a resource method annotated with the {@code @Suspend} annotation declares
 * a {@code void} return type, but it is not a hard requirement to do so. Users should however
 * be aware that any response value returned from the {@code @Suspend}-annotated or
 * programmatically suspended resource method is ignored by the framework:
 * </p>
 *
 * <pre>
 * &#64;Path("/messages/next")
 * public class SimpleAsyncEventResource {
 *     &#64;Context ExecutionContext ctx;
 *     &hellip;
 *     &#64;GET
 *     &#64;Suspend
 *     public String suspendedMethodA(AsynchronousResponse ar) throws InterruptedException {
 *         &hellip;
 *         return "This response will be ignored.";
 *     }
 *     &hellip;
 *     &#64;GET
 *     public String suspendedMethodB(AsynchronousResponse ar) throws InterruptedException {
 *         ctx.suspend();
 *         &hellip;
 *         return "This response will be ignored.";
 *     }
 *     &hellip;
 * }
 * </pre>
 *
 * <p>
 * By default there is {@link #NEVER no suspend timeout set} when suspending a request processing.
 * In such case the processing is suspended indefinitely. Suspend timeout can be specified either
 * declaratively using the {@code &#64;Suspend} annotation properties or programmatically using
 * one of the {@code ExecutionContext.suspend(...)} method. Once suspended, the request processing
 * suspend timeout can be further updated using the
 * {@link javax.ws.rs.core.AsynchronousResponse#setSuspendTimeout(long, TimeUnit)} method.
 * </p>
 * <p>
 * In case a request processing is suspended with a positive timeout value, the processing is resumed
 * when the specified timeout threshold is reached unless explicitly resumed before the timeout has
 * expired. In case of the suspend timeout the request processing is resumed using the
 * {@link javax.ws.rs.core.AsynchronousResponse#getFallbackResponse() fall-back response data} if set.
 * Otherwise, if no fall-back response has been specified, a {@link WebApplicationException} is raised
 * with a HTTP {@code 503} error status (Service unavailable). Fallback response data can be
 * programmatically customized using the
 * {@link javax.ws.rs.core.AsynchronousResponse#setFallbackResponse(java.lang.Object)} method.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Suspend {

    /**
     * Constant specifying no suspend timeout value.
     */
    public static final long NEVER = 0;

    /**
     * Suspend timeout value in the given {@link #timeUnit() time unit}. A default
     * value is {@link #NEVER no timeout}. Similarly, any explicitly set value
     * lower then or equal to zero will be treated as a "no timeout" value.
     */
    long timeOut() default NEVER;

    /**
     * The suspend timeout time unit. Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
