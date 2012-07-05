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
package javax.ws.rs.container;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * A dynamic ({@link PostMatching post-matching}) filter or interceptor binding
 * provider.
 *
 * Dynamic binding provider is used by JAX-RS runtime to provide a the filter or
 * interceptor that shall be applied to a particular resource class and method and
 * overrides any annotation-based binding definitions defined on the returned
 * resource filter or interceptor instance.
 * <p>
 * Providers implementing this interface MAY be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} annotation in order to be
 * discovered by JAX-RS runtime when scanning for resources and providers.
 * This provider types is supported only as part of the Server API.
 * </p>
 *
 * @author Santiago Pericas-Geertsen
 * @author Bill Burke
 * @author Marek Potociar
 * @see javax.ws.rs.NameBinding
 * @since 2.0
 */
// TODO should the binding priority defined here or on the returned filter?
// TODO should we allow BindingPriority to be set on the method (a class implementing more filter interfaces)?
public interface DynamicBinder {

    /**
     * Get the filter or interceptor instances or classes that should be bound to the
     * particular resource method. May return {@code null}.
     * <p>
     * The returned provider instances or classes is expected to be implementing one
     * or more of the following interfaces:
     * </p>
     * <ul>
     * <li>{@link ContainerRequestFilter}</li>
     * <li>{@link ContainerResponseFilter}</li>
     * <li>{@link ReaderInterceptor}</li>
     * <li>{@link WriterInterceptor}</li>
     * </ul>
     * A provider instance or class that does not implement any of the interfaces
     * above is ignored and a {@link java.util.logging.Level#WARNING warning}
     * message is logged.
     * <p />
     * <p>
     * If any of the returned objects is a {@link Class Class&lt;P&gt;}, the JAX-RS
     * runtime will resolve the class to an instance of type {@code P} by first looking
     * at the already registered provider instances.
     * If there is already a provider instance of the class registered, the JAX-RS
     * runtime will use it, otherwise a new provider instance of the class will be
     * instantiated, injected and registered by the JAX-RS runtime.
     * </p>
     * <p>
     * In case resolving of a provider class returned in the result to an instance fails
     * for any reason, the dynamically bound provider class is ignored and
     * a {@link java.util.logging.Level#WARNING warning} message is logged.
     * </p>
     * <p>
     * The method is called during a (sub)resource method discovery phase (typically
     * once per each discovered (sub)resource method) to return provider instances
     * or classes that should be bound to a particular (sub)resource method identified
     * by the supplied {@link ResourceInfo resource information}.
     * </p>
     *
     * @param resourceInfo resource class and method information.
     * @return filter or interceptor instances or classes that should be dynamically bound
     *         to the (sub)resource method or {@code null} otherwise.
     */
    public Iterable<?> getBoundProvider(ResourceInfo resourceInfo);
}
