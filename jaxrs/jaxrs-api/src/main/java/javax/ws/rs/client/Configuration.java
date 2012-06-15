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

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Represents inheritable configuration of the main client-side JAX-RS components,
 * such as {@link Client}, {@link WebTarget}, {@link Invocation.Builder Invocation Builder}
 * or {@link Invocation}.
 * <p />
 * Configuration is inherited from a parent component to a child component.
 * When creating new {@link WebTarget resource targets} using a {@link Client} instance,
 * the configuration of the {@code Client} instance is inherited by the child target
 * instances being created. Similarly, when creating new
 * {@code Invocation.Builder invocation builders} or derived resource targets
 * using a parent target instance, the configuration of the parent target is
 * inherited by the child instances being created.
 * <p/>
 * The inherited configuration on a child instance reflects the state of the parent
 * configuration at the time of the child instance creation. Once the child instance
 * is created its configuration is detached from the parent configuration. This means
 * that any subsequent changes in the parent configuration do not affect
 * the configuration of previously created child instances.
 * <p />
 * Once the child instance is created, it's configuration can be further customized
 * using the provided set of instance configuration mutator methods. A change
 * made in the configuration of a child instance does not affect the configuration
 * of its parent, for example:
 * <pre>
 *   Client client = ClientFactory.newClient();
 *   client.configuration().setProperty("FOO_PROPERTY", "FOO_VALUE");
 *
 *   // inherits the configured "FOO_PROPERTY" from the client instance
 *   WebTarget resourceTarget = client.target("http://examples.jaxrs.com/");
 *
 *   // does not modify the client instance configuration
 *   resourceTarget.configuration().enable(new BarFeature());
 * </pre>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Configuration {

    /**
     * Get the immutable bag of configuration properties.
     *
     * @return the immutable view of configuration properties.
     * @see Configuration
     */
    Map<String, Object> getProperties();

    /**
     * Get the value for the property with a given name.
     *
     * @param name property name.
     * @return the property value for the specified property name or {@code null}
     *         if the property with such name is not configured.
     * @see Configuration
     */
    Object getProperty(String name);

    /**
     * Get the immutable set of enabled features.
     *
     * @return the enabled feature set. The returned value shall never be {@code null}.
     */
    Collection<Feature> getFeatures();

    /**
     * Get the immutable set of registered provider classes to be instantiated,
     * injected and utilized in the scope of the configured instance.
     * <p />
     * A provider class is a Java class with a {@link javax.ws.rs.ext.Provider}
     * annotation declared on the class that implements a specific service
     * interface.
     *
     * @return the immutable set of registered provider classes. The returned
     *         value shall never be {@code null}.
     * @see #getProviderInstances()
     */
    Set<Class<?>> getProviderClasses();

    /**
     * Get the immutable set of registered provider instances to be utilized by
     * the configured instance.
     * <p />
     * When the configured instance is initialized the set of provider instances
     * will be combined and take precedence over the instantiated registered provider
     * classes.
     *
     * @return the immutable set of registered provider instances. The returned
     *         value shall never be {@code null}.
     * @see #getProviderClasses()
     */
    Set<Object> getProviderInstances();

    /**
     * Replace the existing configuration state with the configuration state of
     * the externally provided configuration.
     *
     * @param configuration configuration to be used to update the instance.
     * @return the updated configuration.
     */
    Configuration update(Configuration configuration);

    /**
     * Register a {@link Feature feature} or a provider class to be instantiated
     * and used in the scope of the configured instance.
     * <p>
     * As opposed to the providers registered by the
     * {@link #register(java.lang.Object) provider instances}, providers
     * registered using this method are instantiated and properly injected
     * by the JAX-RS implementation provider. In case of a conflict between
     * a registered provider instance and instantiated registered provider class,
     * the registered provider instance takes precedence and the registered provider
     * class will not be instantiated in such case.
     * </p>
     * <p>
     * In case the registered provider is a client-side {@link Feature feature},
     * this {@code Configuration} object instantiates the feature and invokes the
     * {@link Feature#onEnable(javax.ws.rs.client.Configuration)} method and
     * lets the feature update it's internal configuration state. If the invocation
     * of {@link Feature#onEnable(javax.ws.rs.client.Configuration)} returns {@code true}
     * the feature is added to the {@link #getFeatures() collection of enabled features},
     * otherwise the feature instance is discarded.
     * </p>
     *
     * @param providerClass provider class to be instantiated and used in the scope
     *                      of the configured instance.
     * @return the updated configuration.
     * @see #getProviderClasses()
     */
    Configuration register(Class<?> providerClass);

    /**
     * Register a {@link Feature feature} or a provider ("singleton") instance to be used
     * in the scope of the configured instance.
     * <p>
     * As opposed to the providers registered by the
     * {@link #register(java.lang.Class) provider classes}, provider instances
     * registered using this method are used "as is, i.e. are not managed or
     * injected by the JAX-RS implementation provider. In case of a conflict
     * between a registered provider instance and instantiated registered provider
     * class, the registered provider instance takes precedence and the registered
     * provider class will not be instantiated in such case.
     * </p>
     * <p>
     * In case the registered provider is a client-side {@link Feature feature},
     * this {@code Configuration} object invokes the
     * {@link Feature#onEnable(javax.ws.rs.client.Configuration)} method and
     * lets the feature update it's internal configuration state. If the invocation
     * of {@link Feature#onEnable(javax.ws.rs.client.Configuration)} returns {@code true}
     * the feature is added to the {@link #getFeatures() collection of enabled features},
     * otherwise the feature instance is discarded.
     * </p>
     *
     * @param provider a provider instance to be registered in the scope of the configured
     *                 instance.
     * @return the updated configuration.
     * @see #getProviderInstances()
     */
    Configuration register(Object provider);

    /**
     * Set new configuration properties replacing all previously set properties.
     *
     * @param properties new set of configuration properties. The content of
     *                   the map will replace any existing properties set on the configurable
     *                   instance.
     * @return the updated configuration.
     * @see Configuration
     */
    Configuration setProperties(Map<String, ? extends Object> properties);

    /**
     * Set the new configuration property, if already set, the existing value of
     * the property will be updated. Setting a {@code null} value into a property
     * effectively removes the property from the property bag.
     *
     * @param name  property name.
     * @param value (new) property value. {@code null} value removes the property
     *              with the given name.
     * @return the updated configuration.
     * @see Configuration
     */
    Configuration setProperty(String name, Object value);
}
