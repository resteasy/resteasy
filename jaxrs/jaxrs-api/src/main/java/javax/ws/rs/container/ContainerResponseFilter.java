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
 * An extension interface implemented by container response filters.
 * <p>
 * By default, i.e. if no {@link javax.ws.rs.NameBinding name binding} is applied
 * to the filter implementation class, the filter instance is applied globally to
 * any outgoing response.
 * If there is a {@code @NameBinding} annotation applied to the filter, the filter
 * will only be executed for a response for which the request has been matched to
 * a {@link javax.ws.rs.HttpMethod resource or sub-resource method} AND the method
 * or the whole custom {@link javax.ws.rs.core.Application JAX-RS Application} class
 * is bound to the same name-binding annotation.
 * </p>
 * <p>
 * Implement a name-bound response filter in cases when you want limit the filter
 * functionality to a matched resource or resource method. In other cases,
 * when the filter should be applied globally to any outgoing response, implement an
 * unbound, global response filter.
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
 * @see javax.ws.rs.container.ContainerRequestFilter
 * @since 2.0
 */
public interface ContainerResponseFilter {

    /**
     * Filter method called after a response has been provided for a request
     * (either by a {@link ContainerRequestFilter request filter} or by a
     * matched resource method.
     * <p>
     * Filters in the filter chain are ordered according to their {@code javax.annotation.Priority}
     * class-level annotation value.
     * </p>
     *
     * @param requestContext  request context.
     * @param responseContext response context.
     * @throws IOException if an I/O exception occurs.
     */
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException;
}
