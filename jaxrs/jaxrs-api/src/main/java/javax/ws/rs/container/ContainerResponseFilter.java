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
 * An extension interface implemented by container response filters.
 * <p>
 * By default, i.e. if no {@link javax.ws.rs.NameBinding name binding} is applied
 * to the filter implementation class, the filter instance is applied globally,
 * however only for the responses for which the incoming request has been matched
 * to a particular resource by JAX-RS runtime.
 * If there is a {@link javax.ws.rs.NameBinding &#64;NameBinding} annotation
 * applied to the filter, the filter will also be executed at the <i>post-match</i>
 * response extension point, but only in case the matched {@link javax.ws.rs.HttpMethod
 * resource or sub-resource method} is bound to the same name-binding annotation.
 * </p>
 * <p>
 * In case the filter should be applied at the <i>pre-match</i> response extension point,
 * i.e. globaly for any response, regardless of whether the originating request has been
 * matched to a particular {@link javax.ws.rs.HttpMethod resource or sub-resource method}
 * or not, the filter MUST be annotated with a {@link PreMatching &#64;PreMatching}
 * annotation.
 * </p>
 * <p>
 * Implement a name-bound or global response filter in cases when you want limit
 * the filter functionality to a particular resource or resource method or if you
 * depend on a matched resource information in your filter processing. In other cases,
 * when the filter should be applied globally to all responses, even in those
 * cases when a request has not been matched to a resource implement a pre-matching
 * response filter.
 * </p>
 * <p>
 * Filters implementing this interface must be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} to be discovered by the JAX-RS
 * runtime. Container response filter instances may also be discovered and
 * bound {@link DynamicFeature dynamically} to particular resource methods.
 * </p>
 *
 * @author Marek Potociar
 * @author Santiago Pericas-Geertsen
 * @see PreMatching
 * @see javax.ws.rs.container.ContainerRequestFilter
 * @since 2.0
 */
public interface ContainerResponseFilter {

    /**
     * Filter method called after a response has been provided for a request
     * (either by a {@link ContainerRequestFilter request filter} or by a
     * matched resource method.
     * <p>
     * Filters in the filter chain are ordered according to their binding
     * priority (see {@link javax.ws.rs.BindingPriority}).
     * </p>
     *
     * @param requestContext  request context.
     * @param responseContext response context.
     * @throws IOException if an I/O exception occurs.
     * @see PreMatching
     */
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException;
}
