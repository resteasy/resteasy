/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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
 * <p>
 * By default, i.e. if no {@link javax.ws.rs.NameBinding name binding} is applied
 * to the filter implementation class, the filter instance is applied globally,
 * however only after the incoming request has been matched to a particular resource
 * by JAX-RS runtime.
 * If there is a {@link javax.ws.rs.NameBinding &#64;NameBinding} annotation
 * applied to the filter, the filter will also be executed at the <i>post-match</i>
 * request extension point, but only in case the matched {@link javax.ws.rs.HttpMethod
 * resource or sub-resource method} is bound to the same name-binding annotation.
 * </p>
 * <p>
 * In case the filter should be applied at the <i>pre-match</i> extension point,
 * i.e. before any request matching has been performed by JAX-RS runtime, the
 * filter MUST be annotated with a {@link PreMatching &#64;PreMatching} annotation.
 * </p>
 * <p>
 * Use a pre-match request filter to update the input to the JAX-RS matching algorithm,
 * e.g., the HTTP method, Accept header, return cached responses etc. Otherwise,
 * the use of a request filter invoked at the <i>post-match</i> request extension point
 * (after a successful resource method matching) is recommended.
 * </p>
 * <p>
 * Filters implementing this interface must be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} to be discovered by the JAX-RS
 * runtime. Container request filter instances may also be discovered and
 * bound {@link DynamicFeature dynamically} to particular resource methods.
 * </p>
 *
 * @author Marek Potociar
 * @author Santiago Pericas-Geertsen
 * @see PreMatching
 * @see javax.ws.rs.container.ContainerResponseFilter
 * @since 2.0
 */
public interface ContainerRequestFilter {

    /**
     * Filter method called before a request has been dispatched to a resource.
     *
     * <p>
     * Filters in the filter chain are ordered according to their {@code javax.annotation.Priority}
     * class-level annotation value.
     * If a request filter produces a response by calling {@link ContainerRequestContext#abortWith}
     * method, the execution of the (either pre-match or post-match) request filter
     * chain is stopped and the response is passed to the corresponding response
     * filter chain (either pre-match or post-match). For example, a pre-match
     * caching filter may produce a response in this way, which would effectively
     * skip any post-match request filters as well as post-match response filters.
     * Note however that a responses produced in this manner would still be processed
     * by the pre-match response filter chain.
     * </p>
     *
     * @param requestContext request context.
     * @throws IOException if an I/O exception occurs.
     * @see PreMatching
     */
    public void filter(ContainerRequestContext requestContext) throws IOException;
}
