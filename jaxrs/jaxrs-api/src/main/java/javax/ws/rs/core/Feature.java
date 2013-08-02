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
 * A feature extension contract.
 *
 * Typically encapsulates a concept or facility that involves configuration of multiple providers
 * (e.g. filters or interceptors) and/or properties.
 * <p>
 * A {@code Feature} is a special type of JAX-RS configuration meta-provider. Once a feature is registered,
 * it's {@link #configure(FeatureContext)} method is invoked during JAX-RS runtime configuration and bootstrapping
 * phase allowing the feature to further configure the runtime context in which it has been registered.
 * From within the invoked {@code configure(...)} method a feature may provide additional runtime configuration
 * for the facility or conceptual domain it represents, such as registering additional contract providers,
 * including nested features and/or specifying domain-specific properties.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Feature {

    /**
     * A call-back method called when the feature is to be enabled in a given
     * runtime configuration scope.
     *
     * The responsibility of the feature is to properly update the supplied runtime configuration context
     * and return {@code true} if the feature was successfully enabled or {@code false} otherwise.
     * <p>
     * Note that under some circumstances the feature may decide not to enable itself, which
     * is indicated by returning {@code false}. In such case the configuration context does
     * not add the feature to the collection of enabled features and a subsequent call to
     * {@link Configuration#isEnabled(Feature)} or {@link Configuration#isEnabled(Class)} method
     * would return {@code false}.
     * </p>
     *
     * @param context configurable context in which the feature should be enabled.
     * @return {@code true} if the feature was successfully enabled, {@code false}
     *         otherwise.
     */
    public boolean configure(FeatureContext context);
}
