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
 * A request processing callback that receives request processing completion events.
 * <p>
 * A completion callback is invoked when the whole request processing is over, i.e.
 * once a response for the request has been processed and sent back to the client
 * or in when an unmapped exception or error is being propagated to the container.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface CompletionCallback {
    /**
     * A completion callback notification method that will be invoked when the request
     * processing is finished, after a response is processed and is sent back to the
     * client or when an unmapped throwable has been propagated to the hosting I/O
     * container.
     * <p>
     * An unmapped throwable is propagated to the hosting I/O container in case no
     * {@link javax.ws.rs.ext.ExceptionMapper exception mapper} has been found for
     * a throwable indicating a request processing failure.
     * In this case a non-{@code null} unmapped throwable instance is passed to the method.
     * Note that the throwable instance represents the actual unmapped exception thrown
     * during the request processing, before it has been wrapped into an I/O container-specific
     * exception that was used to propagate the throwable to the hosting I/O container.
     * </p>
     *
     * @param throwable is {@code null}, if the request processing has completed with a response
     *                  that has been sent to the client. In case the request processing resulted
     *                  in an unmapped exception or error that has been propagated to the hosting
     *                  I/O container, this parameter contains the unmapped exception instance.
     */
    public void onComplete(Throwable throwable);
}
