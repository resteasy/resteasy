/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs;

/**
 * A collection of built-in priority constants for the JAX-RS components that are supposed to be
 * ordered based on their {@code javax.annotation.Priority} class-level annotation value when used
 * or applied by JAX-RS runtime.
 * <p>
 * For example, JAX-RS filters and interceptors are grouped in chains for each of the message
 * processing extension points: Pre, PreMatch, Post as well as ReadFrom and WriteTo.
 * Each of these chains is sorted based on priorities which are represented as integer numbers.
 * All chains, except Post, are sorted in ascending order; the lower the number the higher the priority.
 * The Post filter chain is sorted in descending order to ensure that response filters are executed in
 * <em>reverse order</em>.
 * </p>
 * <p>
 * JAX-RS components that belong to the same priority class (same integer value) are executed in an
 * implementation-defined manner. By default, when the {@code @Priority} annotation is absent on a component,
 * for which a priority should be applied, the {@link Priorities#USER} priority value is used.
 * </p>
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @since 2.0
 */
public final class Priorities {

    private Priorities() {
        // prevents construction
    }

    /**
     * Security authentication filter/interceptor priority.
     */
    public static final int AUTHENTICATION = 1000;
    /**
     * Security authorization filter/interceptor priority.
     */
    public static final int AUTHORIZATION = 2000;
    /**
     * Header decorator filter/interceptor priority.
     */
    public static final int HEADER_DECORATOR = 3000;
    /**
     * Message encoder or decoder filter/interceptor priority.
     */
    public static final int ENTITY_CODER = 4000;
    /**
     * User-level filter/interceptor priority.
     */
    public static final int USER = 5000;
}
