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
package javax.ws.rs.container;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Inject a suspended {@link AsyncResponse} into a parameter of an invoked
 * JAX-RS {@link javax.ws.rs.HttpMethod resource or sub-resource method}.
 *
 * The injected {@code AsyncResponse} instance is bound to the processing
 * of the active request and can be used to resume the request processing when
 * a response is available.
 * <p>
 * By default there is {@link AsyncResponse#NO_TIMEOUT no suspend timeout set} and
 * the asynchronous response is suspended indefinitely. The suspend timeout as well
 * as a custom {@link TimeoutHandler timeout handler} can be specified programmatically
 * using the {@link AsyncResponse#setTimeout(long, TimeUnit)} and
 * {@link AsyncResponse#setTimeoutHandler(TimeoutHandler)} methods. For example:
 * <p/>
 * <pre>
 *  &#64;Stateless
 *  &#64;Path("/")
 *  public class MyEjbResource {
 *    &hellip;
 *    &#64;GET
 *    &#64;Asynchronous
 *    public void longRunningOperation(&#64;Suspended AsyncResponse ar) {
 *      ar.setTimeoutHandler(customHandler);
 *      ar.setTimeout(10, TimeUnit.SECONDS);
 *      final String result = executeLongRunningOperation();
 *      ar.resume(result);
 *    }
 *
 *    private String executeLongRunningOperation() { &hellip; }
 *  }
 * </pre>
 * <p>
 * A resource or sub-resource method that injects a suspended instance of an
 * {@code AsyncResponse} using the {@code @Suspended} annotation is expected
 * be declared to return {@code void} type. Methods that inject asynchronous
 * response instance using the {@code @Suspended} annotation and declare a
 * return type other than {@code void} MUST be detected by the JAX-RS runtime and
 * a warning message MUST be logged. Any response value returned from such resource
 * or sub-resource method MUST be ignored by the framework:
 * </p>
 * <pre>
 * &#64;Path("/messages/next")
 * public class MessagingResource {
 *     &hellip;
 *     &#64;GET
 *     public String readMessage(&#64;Suspended AsyncResponse ar) {
 *         suspended.put(ar);
 *         return "This response will be ignored.";
 *     }
 *     &hellip;
 * }
 * </pre>
 *
 * @author Marek Potociar
 * @since 2.0
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Suspended {
}
