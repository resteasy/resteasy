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

import javax.ws.rs.core.Response;

/**
 * Asynchronous request processing lifecycle callback that receives suspended
 * {@link AsyncResponse asynchronous response} resume events.
 * <p>
 * A resume callback may receive resume events (from an asynchronous response
 * the callback was registered with) as a result of one of the following
 * actions:
 * <ul>
 * <li>asynchronous response has been resumed directly, by calling
 * {@link AsyncResponse#resume(Object)} or {@link AsyncResponse#resume(Throwable)}
 * method</li>
 * <li>asynchronous response processing was cancelled resulting in an asynchronous response
 * instance being resumed with a generated error response</li>
 * <li>an {@link TimeoutHandler unhandled} suspend time-out event has occurred
 * resulting in an asynchronous response instance being resumed with a default
 * time-out exception</li>
 * </ul>
 * In all of the cases above, a resume event will be generated for the suspended asynchronous
 * response and all callbacks associated with the asynchronous response that implement
 * {@code ResumeCallback} interface will be invoked before any response processing is started,
 * e.g. before any exception mapping or response filtering occurs.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface ResumeCallback {
    /**
     * A resume callback notification method that will be invoked when the asynchronous
     * response is about to be resumed with a JAX-RS response instance.
     *
     * This means the method is invoked BEFORE any response processing (e.g. response filters
     * execution) starts.
     * <p>
     * Callback implementations may use check whether resuming asynchronous response
     * {@link AsyncResponse#isCancelled() has been cancelled} or not. In case of cancellation,
     * the JAX-RS response will be the request cancellation response sent back to the client
     * (see {@link javax.ws.rs.container.AsyncResponse#cancel()}).
     * </p>
     *
     * @param resuming asynchronous response to be resumed.
     * @param response response used to resume the asynchronous response.
     */
    public void onResume(AsyncResponse resuming, Response response);

    /**
     * A resume callback notification method that will be invoked in case the asynchronous
     * response is being resumed by an error (e.g. in case of a time-out event).
     *
     * This means the method is invoked BEFORE any response processing (e.g. exception mapping or
     * response filters execution) starts.
     *
     * @param resuming asynchronous response to be resumed.
     * @param error error used to resume the asynchronous response.
     */
    public void onResume(AsyncResponse resuming, Throwable error);
}
