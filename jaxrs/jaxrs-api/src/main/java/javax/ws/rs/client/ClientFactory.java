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
package javax.ws.rs.client;

import java.net.URL;

/**
 * Main entry point to the client API used to bootstrap {@link javax.ws.rs.client.Client}
 * instances.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public abstract class ClientFactory {

    /**
     * Name of the property identifying the {@link javax.ws.rs.ext.RuntimeDelegate} implementation
     * to be returned from {@link javax.ws.rs.ext.RuntimeDelegate#getInstance()}.
     */
    public static final String JAXRS_DEFAULT_CLIENT_FACTORY_PROPERTY =
            "javax.ws.rs.ext.ClientFactory";
    /**
     * Default client factory implementation class name.
     */
    private static final String JAXRS_DEFAULT_CLIENT_FACTORY =
            "org.glassfish.jersey.client.JerseyClientFactory";

    private static ClientFactory getFactory() {
        try {
            Object delegate =
                    FactoryFinder.find(JAXRS_DEFAULT_CLIENT_FACTORY_PROPERTY,
                    JAXRS_DEFAULT_CLIENT_FACTORY);
            if (!(delegate instanceof ClientFactory)) {
                Class pClass = ClientFactory.class;
                String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast"
                        + delegate.getClass().getClassLoader().getResource(classnameAsResource)
                        + "to" + targetTypeURL.toString());
            }
            return (ClientFactory) delegate;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create new client instance using the default client builder factory provided
     * by the JAX-RS implementation provider.
     *
     * @return new client instance.
     */
    public static Client newClient() {
        return getFactory().getClient();
    }

    /**
     * Create new configured client instance using the default client builder factory
     * provided by the JAX-RS implementation provider.
     *
     * @param configuration data used to provide initial configuration for the new
     *     client instance.
     * @return new configured client instance.
     */
    public static Client newClient(final Configuration configuration) {
        return getFactory().getClient(configuration);
    }

    /**
     * Get a new client instance.
     *
     * @return a new client instance.
     */
    protected abstract Client getClient();

    /**
     * Get a new pre-configured client instance.
     *
     * @param configuration data used to provide initial configuration for
     *     the new client instance.
     * @return a new client instance.
     */
    protected abstract Client getClient(final Configuration configuration);
}
