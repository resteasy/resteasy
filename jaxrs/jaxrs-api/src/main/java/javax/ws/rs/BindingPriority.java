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
package javax.ws.rs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Filters and interceptors are grouped in chains for each of the extension
 * points: Pre, PreMatch, Post as well as ReadFrom and WriteTo.
 * Each of these chains is sorted by binding priorities which are represented
 * as integer numbers.
 * All chains except Post are sorted in ascending order; the lower
 * the number the higher the priority. The Post filter chain is sorted
 * in descending order to ensure that response filters are executed in
 * <em>reverse order</em>.</p>
 *
 * <p>This class defines a few built-in priority classes. Filters and interceptors
 * that belong to the same priority class (same integer value) are executed
 * in an implementation-defined manner. By default, i.e. when
 * this annotation is absent, a filter or interceptor is defined in the
 * {@link #USER} class.</p>
 *
 * @author Santiago Pericas-Geertsen
 * @since 2.0
 */
@Target(ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BindingPriority {

    public static final int SECURITY = 100;
    public static final int HEADER_DECORATOR = 200;
    public static final int DECODER = 300;
    public static final int ENCODER = 400;
    public static final int USER = 500;

    /**
     * Priority defined for a filter or interceptor.
     *
     * @return filter or interceptor priority
     */
    int value();
}
