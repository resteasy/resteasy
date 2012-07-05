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
package javax.ws.rs.client;

/**
 * Callback that can be implemented to receive the asynchronous processing
 * events from the invocation processing.
 *
 * @param <RESPONSE> response type. It can be either a general-purpose
 *                   {@link javax.ws.rs.core.Response} or the anticipated response entity
 *                   type.
 * @author Marek Potociar
 * @since 2.0
 */
public interface InvocationCallback<RESPONSE> {

    /**
     * Called when the invocation was successfully completed. Note that this does
     * not necessarily mean the response has bean fully read. This depends on the
     * expected invocation callback response type.
     *
     * @param response response data.
     */
    public abstract void completed(RESPONSE response);

    /**
     * Called when the invocation has failed for any reason.
     *
     * <p>
     * Note that the provided {@link ClientException} may contain a cause represented by nested
     * {@link javax.ws.rs.WebApplicationException} or one of its subclasses in case the response
     * status code is not {@link javax.ws.rs.core.Response.Status.Family#SUCCESSFUL successful}
     * and the generic callback type is not {@link javax.ws.rs.core.Response}.
     * </p>
     *
     * @param error contains failure details.
     */
    public abstract void failed(ClientException error);
}
