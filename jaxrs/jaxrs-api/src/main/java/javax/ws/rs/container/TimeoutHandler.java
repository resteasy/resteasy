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

/**
 * Asynchronous response suspend time-out handler.
 *
 * JAX-RS users may utilize this callback interface to provide
 * custom resolution of time-out events.
 * <p>
 * By default, JAX-RS runtime generates a {@link javax.ws.rs.WebApplicationException}
 * with a {@link javax.ws.rs.core.Response.Status#SERVICE_UNAVAILABLE HTTP 503
 * (Service unavailable)} error response status code. A custom time-out handler
 * may be {@link AsyncResponse#setTimeoutHandler(TimeoutHandler) set} on an
 * asynchronous response instance to provide custom time-out event resolution.
 * </p>
 * <p>
 * In case of a suspend time-out event, a custom time-out handler takes typically one
 * of the following actions:
 * <ul>
 * <li>Resumes the suspended asynchronous response using a {@link AsyncResponse#resume(Object)
 * custom response} or a {@link AsyncResponse#resume(Throwable) custom exception}</li>
 * <li>Cancels the response by calling one of the {@link AsyncResponse} {@code cancel(...)}
 * methods.</li>
 * <li>Extends the suspend period of the response by
 * {@link AsyncResponse#setTimeout(long, java.util.concurrent.TimeUnit)
 * setting a new suspend time-out}</li>
 * </ul>
 * If the registered time-out handler does not take any of the actions above, the
 * default time-out event processing continues and the response is resumed with
 * a generated {@code WebApplicationException} containing the HTTP 503 status code.
 * </p>
 * <p>
 * Following example illustrates the use of a custom {@code TimeoutHandler}:
 * </p>
 * <pre>
 * public class MyTimeoutHandler implements TimeoutHandler {
 *     &hellip;
 *     public void handleTimeout(AsyncResponse ar) {
 *         if (keepSuspended) {
 *             ar.setTimeout(10, SECONDS);
 *         } else if (cancel) {
 *             ar.cancel(retryPeriod);
 *         } else {
 *             ar.resume(defaultResponse);
 *         }
 *     }
 *     &hellip;
 * }
 *
 * &#64;Path("/messages/next")
 * public class MessagingResource {
 *     &hellip;
 *     &#64;GET
 *     public void readMessage(&#64;Suspended AsyncResponse ar) {
 *         ar.setTimeoutHandler(new MyTimeoutHandler());
 *         suspended.put(ar);
 *     }
 *     &hellip;
 * }
 * </pre>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface TimeoutHandler {

    /**
     * Invoked when the suspended asynchronous response is about to time out.
     *
     * Implementing time-out handlers may use the callback method to change the
     * default time-out strategy defined by JAX-RS specification (see
     * {@link javax.ws.rs.container.AsyncResponse} API documentation).
     * <p>
     * A custom time-out handler may decide to either
     * <ul>
     * <li>resume the suspended response using one of it's {@code resume(...)} methods,</li>
     * <li>cancel the suspended response using one of it's {@code cancel(...)} methods, or</li>
     * <li>extend the suspend period by {@link AsyncResponse#setTimeout(long, java.util.concurrent.TimeUnit)
     * setting a new suspend time-out}</li>
     * </ul>
     * </p>
     * In case the time-out handler does not take any of the actions mentioned above,
     * a default time-out strategy is executed by the JAX-RS runtime.
     *
     * @param asyncResponse suspended asynchronous response that is timing out.
     */
    public void handleTimeout(AsyncResponse asyncResponse);
}
