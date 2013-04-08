/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
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
import java.security.KeyStore;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Main entry point to the client API used to bootstrap {@link javax.ws.rs.client.Client}
 * instances.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public abstract class ClientBuilder implements Configurable<ClientBuilder> {

    /**
     * Name of the property identifying the {@link ClientBuilder} implementation
     * to be returned from {@link ClientBuilder#newBuilder()}.
     */
    public static final String JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY =
            "javax.ws.rs.client.ClientBuilder";
    /**
     * Default client builder implementation class name.
     */
    private static final String JAXRS_DEFAULT_CLIENT_BUILDER =
            "org.glassfish.jersey.client.JerseyClientBuilder";

    /**
     * Allows custom implementations to extend the {@code ClientBuilder} class.
     */
    protected ClientBuilder() {
    }

    /**
     * Create a new {@code ClientBuilder} instance using the default client builder
     * implementation class provided by the JAX-RS implementation provider.
     *
     * @return new client builder instance.
     */
    public static ClientBuilder newBuilder() {
        try {
            Object delegate =
                    FactoryFinder.find(JAXRS_DEFAULT_CLIENT_BUILDER_PROPERTY,
                            JAXRS_DEFAULT_CLIENT_BUILDER);
            if (!(delegate instanceof ClientBuilder)) {
                Class pClass = ClientBuilder.class;
                String classnameAsResource = pClass.getName().replace('.', '/') + ".class";
                ClassLoader loader = pClass.getClassLoader();
                if (loader == null) {
                    loader = ClassLoader.getSystemClassLoader();
                }
                URL targetTypeURL = loader.getResource(classnameAsResource);
                throw new LinkageError("ClassCastException: attempting to cast"
                        + delegate.getClass().getClassLoader().getResource(classnameAsResource)
                        + " to " + targetTypeURL);
            }
            return (ClientBuilder) delegate;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create a new {@link Client} instance using the default client builder implementation
     * class provided by the JAX-RS implementation provider.
     *
     * @return new client instance.
     */
    public static Client newClient() {
        return newBuilder().build();
    }

    /**
     * Create a new custom-configured {@link Client} instance using the default client builder
     * implementation class provided by the JAX-RS implementation provider.
     *
     * @param configuration data used to provide initial configuration for the new
     *                      client instance.
     * @return new configured client instance.
     */
    public static Client newClient(final Configuration configuration) {
        return newBuilder().withConfig(configuration).build();
    }

    /**
     * Set the internal configuration state to an externally provided configuration state.
     *
     * @param config external configuration state to replace the configuration of this configurable
     *               instance.
     * @return the updated client builder instance.
     */
    public abstract ClientBuilder withConfig(Configuration config);

    /**
     * Set the SSL context that will be used when creating secured transport connections
     * to server endpoints from {@link WebTarget web targets} created by the client
     * instance that is using this SSL context. The SSL context is expected to have all the
     * security infrastructure initialized, including the key and trust managers.
     * <p>
     * Setting a SSL context instance resets any {@link #keyStore(java.security.KeyStore, char[])
     * key store} or {@link #trustStore(java.security.KeyStore) trust store} values previously
     * specified.
     * </p>
     *
     * @param sslContext secure socket protocol implementation which acts as a factory
     *                   for secure socket factories or {@link javax.net.ssl.SSLEngine
     *                   SSL engines}. Must not be {@code null}.
     * @return an updated client builder instance.
     * @throws NullPointerException in case the {@code sslContext} parameter is {@code null}.
     * @see #keyStore(java.security.KeyStore, char[])
     * @see #keyStore(java.security.KeyStore, String)
     * @see #trustStore
     */
    public abstract ClientBuilder sslContext(final SSLContext sslContext);

    /**
     * Set the client-side key store. Key store contains client's private keys, and the certificates with their
     * corresponding public keys.
     * <p>
     * Setting a key store instance resets any {@link #sslContext(javax.net.ssl.SSLContext) SSL context instance}
     * value previously specified.
     * </p>
     * <p>
     * Note that a custom key store is only required if you want to enable a custom setup of a 2-way SSL connections
     * (client certificate authentication).
     * </p>
     *
     * @param keyStore client-side key store. Must not be {@code null}.
     * @param password client key password. Must not be {@code null}.
     * @return an updated client builder instance.
     * @throws NullPointerException in case any of the supplied parameters is {@code null}.
     * @see #sslContext
     * @see #keyStore(java.security.KeyStore, String)
     * @see #trustStore
     */
    public abstract ClientBuilder keyStore(final KeyStore keyStore, final char[] password);

    /**
     * Set the client-side key store. Key store contains client's private keys, and the certificates with their
     * corresponding public keys.
     * <p>
     * Setting a key store instance resets any {@link #sslContext(javax.net.ssl.SSLContext) SSL context instance}
     * value previously specified.
     * </p>
     * <p>
     * Note that for improved security of working with password data and avoid storing passwords in Java string
     * objects, the {@link #keyStore(java.security.KeyStore, char[])} version of the method can be utilized.
     * Also note that a custom key store is only required if you want to enable a custom setup of a 2-way SSL
     * connections (client certificate authentication).
     * </p>
     *
     * @param keyStore client-side key store. Must not be {@code null}.
     * @param password client key password. Must not be {@code null}.
     * @return an updated client builder instance.
     * @throws NullPointerException in case any of the supplied parameters is {@code null}.
     * @see #sslContext
     * @see #keyStore(java.security.KeyStore, char[])
     * @see #trustStore
     */
    public ClientBuilder keyStore(final KeyStore keyStore, final String password) {
        return keyStore(keyStore, password.toCharArray());
    }

    /**
     * Set the client-side trust store. Trust store is expected to contain certificates from other parties
     * the client is you expect to communicate with, or from Certificate Authorities that are trusted to
     * identify other parties.
     * <p>
     * Setting a trust store instance resets any {@link #sslContext(javax.net.ssl.SSLContext) SSL context instance}
     * value previously specified.
     * </p>
     * <p>
     * In case a custom trust store or custom SSL context is not specified, the trust management will be
     * configured to use the default Java runtime settings.
     * </p>
     *
     * @param trustStore client-side trust store. Must not be {@code null}.
     * @return an updated client builder instance.
     * @throws NullPointerException in case the supplied trust store parameter is {@code null}.
     * @see #sslContext
     * @see #keyStore(java.security.KeyStore, char[])
     * @see #keyStore(java.security.KeyStore, String)
     */
    public abstract ClientBuilder trustStore(final KeyStore trustStore);

    /**
     * Set the hostname verifier to be used by the client to verify the endpoint's hostname against it's
     * identification information.
     *
     * @param verifier hostname verifier.
     * @return an updated client builder instance.
     */
    public abstract ClientBuilder hostnameVerifier(final HostnameVerifier verifier);

    /**
     * Build a new client instance using all the configuration previously specified
     * in this client builder.
     *
     * @return a new client instance.
     */
    public abstract Client build();
}
