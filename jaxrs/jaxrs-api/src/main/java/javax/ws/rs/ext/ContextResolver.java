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
package javax.ws.rs.ext;

/**
 * Contract for a provider that supplies context information to resource
 * classes and other providers. An implementation of this interface must be
 * annotated with {@link Provider}.
 *
 * A <code>ContextResolver</code> implementation may be annotated
 * with {@link javax.ws.rs.Produces} to restrict the media types for
 * which it will be considered suitable.
 *
 * @param <T> type of the context
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see javax.ws.rs.core.Context
 * @see Providers#getContextResolver(java.lang.Class, javax.ws.rs.core.MediaType)
 * @see Provider
 * @see javax.ws.rs.Produces
 * @since 1.0
 */
public interface ContextResolver<T> {

    /**
     * Get a context of type <code>T</code> that is applicable to the supplied
     * type.
     *
     * @param type the class of object for which a context is desired
     * @return a context for the supplied type or <code>null</code> if a
     *         context for the supplied type is not available from this provider.
     */
    T getContext(Class<?> type);
}
