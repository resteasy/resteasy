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

import javax.ws.rs.core.Context;

/**
 * The resource context provides access to instances of resource classes.
 * <p>
 * This interface can be injected using the {@link Context} annotation.
 * </p>
 * <p>
 * The resource context can be utilized when instances of managed resource
 * classes are to be returned by sub-resource locator methods. Such instances
 * will be injected and managed within the declared scope just like instances
 * of root resource classes.
 * </p>
 *
 * @author Marek Potociar
 */
public interface ResourceContext {

    /**
     * Get a resolved instance of a resource or sub-resource class.
     * <p>
     * The resolved resource instance is properly initialized in the context of the
     * current request processing scope. The scope of the resolved resource instance
     * depends on the managing container. For resources managed by JAX-RS container
     * the default scope is per-request.
     * </p>
     *
     * @param <T>           the type of the resource class.
     * @param resourceClass the resource class.
     * @return an instance if it could be resolved, otherwise {@code null}.
     */
    public <T> T getResource(Class<T> resourceClass);

    /**
     * Initialize the resource or sub-resource instance.
     *
     * All JAX-RS injectable fields in the resource instance will be properly initialized in
     * the context of the current request processing scope.
     *
     * @param <T>      resource instance type.
     * @param resource resource instance.
     * @return initialized (same) resource instance.
     */
    public <T> T initResource(T resource);
}
