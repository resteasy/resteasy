/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011 Oracle and/or its affiliates. All rights reserved.
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
 * Marks a request processed by the {@code @Suspend}-annotated {@link HttpMethod JAX-RS resource method}
 * for suspending. Suspended request processing can be resumed using an injectable
 * {@link javax.ws.rs.core.ExecutionContext} instance bound to the processed request:
 *
 * <pre>
 * &#64;Path("/messages/next")
 * public class SimpleAsyncEventResource {
 *     private static final BlockingQueue&lt;ExecutionContext&gt; suspended = new ArrayBlockingQueue&lt;ExecutionContext&gt;(5);
 *     &#64;Context ExecutionContext ctx;
 *
 *     &#64;GET
 *     &#64;Suspend
 *     public void pickUpMessage() throws InterruptedException {
 *         suspended.put(ctx);
 *     }
 *
 *     &#64;POST
 *     public String postMessage(final String message) throws InterruptedException {
 *         suspended.take().resume(message);
 *         return "Message sent";
 *     }
 * }
 * </pre>
 *
 * Using {@code @Suspend} on a resource method is equivalent to calling 
 * {@link javax.ws.rs.core.ExecutionContext#suspend} as the first step upon
 * entering the method.
 * 
 * Typically resource method annotated with {@code @Suspend} annotation declare
 * {@code void} return type, but it is not a hard requirement to do so. Any response
 * value returned from the {@code @Suspend}-annotated resource method is ignored
 * by the framework:
 *
 * <pre>
 * &#64;Path("/messages/next")
 * public class SimpleAsyncEventResource {
 *     &hellip;
 *     &#64;GET
 *     &#64;Suspend
 *     public String pickUpMessage() throws InterruptedException {
 *         suspended.put(ctx);
 *         return "This response will be ignored.";
 *     }
 *     &hellip;
 * }
 * </pre>
 *
 * By default there is {@link #NEVER no suspend timeout set} and request processing is
 * suspended indefinitely. The suspend timeout can be specified using the annotation
 * values. Declaratively specified timeout can be further overridden using one
 * of the {@code suspend(...)} methods in the {@link javax.ws.rs.core.ExecutionContext}
 * programmatic API.
 * <p/>
 * If the request processing was suspended with a positive timeout value, the
 * processing will be resumed once the specified timeout threshold is reached
 * provided the request processing was not explicitly resumed before the
 * suspending has expired. The request processing will be resumed using response
 * data returned by the associated {@link javax.ws.rs.core.ExecutionContext#getResponse()}
 * method. Should the method return {@code null}, a {@link WebApplicationException}
 * is raised with a HTTP 503 error status (Service unavailable). Use
 * {@link javax.ws.rs.core.ExecutionContext#setResponse(java.lang.Object)}
 * method to programmatically customize the default timeout response.
 * <p/>
 * The annotation is ignored if it is used on any method other than JAX-RS
 * resource method.
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
     * value is {@link #NEVER no timeout}.
     */
    long timeOut() default NEVER;

    /**
     * The suspend timeout time unit. Defaults to {@link TimeUnit#MILLISECONDS}.
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
