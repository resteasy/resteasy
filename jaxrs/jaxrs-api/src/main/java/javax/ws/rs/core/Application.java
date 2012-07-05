/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.core;

import java.util.Collections;
import java.util.Set;

/**
 * Defines the components of a JAX-RS application and supplies additional
 * meta-data. A JAX-RS application or implementation supplies a concrete
 * subclass of this abstract class.
 *
 * <p>The implementation-created instance of an Application subclass may be
 * injected into resource classes and providers using
 * {@link javax.ws.rs.core.Context}.<p>
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @since 1.0
 */
public class Application {

    private static final Set<Object> EMPTY_OBJECT_SET = Collections.emptySet();
    private static final Set<Class<?>> EMPTY_CLASS_SET = Collections.emptySet();

    /**
     * Get a set of root resource and provider classes. The default life-cycle
     * for resource class instances is per-request. The default life-cycle for
     * providers is singleton.
     *
     * <p>Implementations should warn about and ignore classes that do not
     * conform to the requirements of root resource or provider classes.
     * Implementations should warn about and ignore classes for which
     * {@link #getSingletons()} returns an instance. Implementations MUST
     * NOT modify the returned set.</p>
     *
     * <p>The default implementation returns an empty set.</p>
     *
     * @return a set of root resource and provider classes. Returning {@code null}
     *         is equivalent to returning an empty set.
     */
    public Set<Class<?>> getClasses() {
        return EMPTY_CLASS_SET;
    }

    /**
     * Get a set of root resource and provider instances. Fields and properties
     * of returned instances are injected with their declared dependencies
     * (see {@link Context}) by the runtime prior to use.
     *
     * <p>Implementations should warn about and ignore classes that do not
     * conform to the requirements of root resource or provider classes.
     * Implementations should flag an error if the returned set includes
     * more than one instance of the same class. Implementations MUST
     * NOT modify the returned set.</p>
     *
     * <p>The default implementation returns an empty set.</p>
     *
     * @return a set of root resource and provider instances. Returning {@code null}
     *         is equivalent to returning an empty set.
     */
    public Set<Object> getSingletons() {
        return EMPTY_OBJECT_SET;
    }
}
