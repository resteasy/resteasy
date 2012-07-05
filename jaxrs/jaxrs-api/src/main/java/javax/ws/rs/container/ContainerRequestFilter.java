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

import java.io.IOException;

/**
 * An extension interface implemented by container request filters.
 *
 * By default, i.e. if no {@link javax.ws.rs.NameBinding name binding} is applied
 * to the filter implementation class, the {@code filter(...)} method is called
 * at the <i>pre-match</i> extension point, i.e. prior to the actual resource
 * matching takes place in the JAX-RS runtime.
 * If there is a {@link javax.ws.rs.NameBinding &#64;NameBinding} annotation
 * applied to the filter, the filter will be executed at the <i>post-match</i>
 * extension point, i.e. after a request was successfully matched with a
 * resource method.
 * <p />
 * Use a pre-match request filter to update the input to the JAX-RS matching algorithm,
 * e.g., the HTTP method, Accept header, return cached responses etc. Otherwise,
 * the use of a request filter invoked at the <i>post-match</i> extension point
 * (after a successful resource method matching) is recommended.
 * <p />
 * Filters implementing this interface must be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} to be discovered by the JAX-RS
 * runtime. Container request filter instances may also be discovered and
 * bound {@link DynamicBinder dynamically} to particular resource methods.
 *
 * @author Marek Potociar
 * @author Santiago Pericas-Geertsen
 * @see javax.ws.rs.container.PostMatching
 * @see javax.ws.rs.container.ContainerResponseFilter
 * @since 2.0
 */
public interface ContainerRequestFilter {

    /**
     * Filter method called before a request has been dispatched to a resource.
     *
     * By default, i.e. if no {@link javax.ws.rs.NameBinding name binding} is applied
     * to the filter implementation class, the {@code filter(...)} method is called
     * at the <i>pre-match</i> extension point, i.e. prior to the actual resource
     * matching takes place in the JAX-RS runtime.
     * If there is a {@link javax.ws.rs.NameBinding &#64;NameBinding} annotation
     * applied to the filter, the filter will be executed at the <i>post-match</i>
     * extension point, i.e. after a request was successfully matched with a
     * resource method.
     * <p />
     * Filters in the filter chain are ordered according to their binding
     * priority (see {@link javax.ws.rs.BindingPriority}). If a request filter
     * produces a response by calling {@link ContainerRequestContext#abortWith}
     * method, the execution of the (either pre-match or post-match) request filter
     * chain is stopped and the response is passed to the corresponding response
     * filter chain (either pre-match or post-match). For example, a pre-match
     * caching filter may produce a response in this way, which would effectively
     * skip any post-match request filters as well as post-match response filters.
     * Note however that a responses produced in this manner would still be processed
     * by the pre-match response filter chain.
     *
     * @param requestContext request context.
     * @throws IOException if an I/O exception occurs.
     * @see javax.ws.rs.container.PostMatching
     */
    public void filter(ContainerRequestContext requestContext) throws IOException;
}
