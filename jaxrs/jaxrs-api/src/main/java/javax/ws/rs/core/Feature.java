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
package javax.ws.rs.core;

/**
 * A feature extension.
 * <p>
 * Typically encapsulates concepts that involve multiple providers
 * (e.g. filters or interceptors) and/or configuration properties.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Feature {

    /**
     * A call-back method called when the feature is to be enabled in a given
     * configurable scope.
     *
     * The responsibility of the feature is to properly update the supplied configurable context
     * and return {@code true} if the feature was successfully enabled or {@code false} otherwise.
     * <p>
     * Note that under some circumstances the feature may decide not to enable itself, which
     * is indicated by returning {@code false}. In such case the configurable context does
     * not internally register the feature in the {@link javax.ws.rs.core.Configurable#getFeatures()
     * collection of enabled features} and the attempt to enable the feature is ignored.
     * </p>
     *
     *
     * @param configurable configurable context in which the feature should be enabled.
     * @return {@code true} if the feature was successfully enabled, {@code false}
     *         otherwise.
     */
    public boolean configure(Configurable configurable);
}
