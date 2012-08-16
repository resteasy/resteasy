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
package javax.ws.rs.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A configurable JAX-RS runtime context.
 *
 * The exact scope of the configurable context is typically determined by a
 * use case scenario in which the configurable context is accessed.
 * <p>
 * A configurable context may be used to retrieve or updated configuration
 * of the bound run-time component. The modification of the context typically
 * involves setting properties or registering new providers and/or features.
 * </p>
 * <h3>Registering providers and/or features.</h3>
 * <p>
 * In some situations a provider class or instance may implement multiple contracts
 * recognized by a JAX-RS implementation (e.g. filter, interceptor or entity provider).
 * By default, the JAX-RS implementation will register the provider as
 * a provider for all the recognized implemented contracts. For example:
 * </p>
 * <pre>
 * public class MyProvider
 *         implements ReaderInterceptor, WriterInterceptor { ... }
 *
 * ...
 *
 * // register MyProvider as a ReaderInterceptor
 * // as well as a WriterInterceptor
 * configuration.register(MyProvider.class);
 * </pre>
 * <p>
 * However there are some situations when the default registration to all the recognized
 * contracts is not desirable. In such case the users may use a version of the {@code register(...)}
 * method that allows to explicitly list the {@code contracts} for which the provider class should
 * be registered, effectively limiting the scope of the provider. For example:
 * </p>
 * <p>
 * <pre>
 * public class ClientLoggingFilter
 *         implements ClientRequestFilter, ClientResponseFilter { ... }
 *
 * ...
 *
 * ClientLoggingFilter loggingFilter = ...;
 * // register loggingFilter as a ClientResponseFilter only
 * configuration.register(loggingFilter, ClientResponseFilter.class);
 * </pre>
 * </p>
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @since 2.0
 */
public interface Configurable {
    /**
     * Get the immutable bag of configuration properties.
     *
     * @return the immutable view of configuration properties.
     */
    public Map<String, Object> getProperties();

    /**
     * Get the value for the property with a given name.
     *
     * @param name property name.
     * @return the property value for the specified property name or {@code null}
     *         if the property with such name is not configured.
     */
    public Object getProperty(String name);

    /**
     * Set new configuration properties replacing all previously set properties.
     *
     * @param properties new set of configuration properties. The content of
     *                   the map will replace any existing properties set on the configurable
     *                   instance.
     * @return the updated configurable instance.
     */
    public Configurable setProperties(Map<String, ?> properties);

    /**
     * Set the new configuration property, if already set, the existing value of
     * the property will be updated. Setting a {@code null} value into a property
     * effectively removes the property from the property bag.
     *
     * @param name  property name.
     * @param value (new) property value. {@code null} value removes the property
     *              with the given name.
     * @return the updated configurable instance.
     */
    public Configurable setProperty(String name, Object value);

    /**
     * Get the immutable set of configured features.
     * <p>
     * The returned set contains the features that have already been successfully
     * {@link Feature#configure(Configurable) configured} in this configuration context.
     * </p>
     *
     * @return the enabled feature set. The returned value shall never be {@code null}.
     * @see Feature#configure(Configurable)
     */
    public Collection<Feature> getFeatures();

    /**
     * Get the immutable set of registered provider classes to be instantiated,
     * injected and utilized in the scope of the configured instance (excluding
     * {@link Feature features}).
     *
     * @return the immutable set of registered provider classes. The returned
     *         value shall never be {@code null}.
     * @see #getProviderInstances()
     */
    public Set<Class<?>> getProviderClasses();

    /**
     * Get the immutable set of registered provider instances to be utilized by
     * the configured instance (excluding {@link Feature features}).
     * <p>
     * When the configured instance is initialized the set of provider instances
     * will be combined and take precedence over the instantiated registered provider
     * classes.
     * </p>
     *
     * @return the immutable set of registered provider instances. The returned
     *         value shall never be {@code null}.
     * @see #getProviderClasses()
     */
    public Set<Object> getProviderInstances();

    /**
     * Register a provider or a {@link Feature feature} class to be instantiated
     * and used in the scope of the configured instance.
     *
     * The registered provider class is registered as a provider of all the recognized
     * JAX-RS or implementation-specific extension contracts.
     * <p>
     * As opposed to the providers registered by the
     * {@link #register(Object)  provider instances}, providers
     * registered using this method are instantiated and properly injected
     * by the JAX-RS implementation provider. In case of a conflict between
     * a registered provider instance and instantiated registered provider class,
     * the registered provider instance takes precedence and the registered provider
     * class will not be instantiated in such case.
     * </p>
     * <p>
     * In case the registered class represents a {@link Feature feature},
     * this {@code Configurable} context instantiates the feature and invokes the
     * {@link Feature#configure(javax.ws.rs.core.Configurable)} method and
     * lets the feature update the configurable context. If the invocation
     * of {@link Feature#configure(javax.ws.rs.core.Configurable)} returns {@code true}
     * the feature is added to the {@link #getFeatures() collection of enabled features},
     * otherwise the feature instance is discarded.
     * </p>
     *
     * @param providerClass provider class to be instantiated and used in the scope
     *                      of the configured instance.
     * @return the updated configurable instance.
     */
    public Configurable register(Class<?> providerClass);

    /**
     * Register a provider or a {@link Feature feature} class to be instantiated and used
     * in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Class)}
     * except that any provider binding priority specified on the provider class using
     * {@link javax.ws.rs.BindingPriority &#64;BindingPriority} annotation is overridden
     * with the supplied {@code bindingPriority} value.
     * </p>
     * <p>
     * Note that in case the binding priority cannot be applied to a particular provider
     * contract registered for the provider class, the supplied {@code bindingPriority} value
     * is ignored.
     * </p>
     *
     * @param providerClass   provider class to be instantiated and used in the scope
     *                        of the configured instance.
     * @param bindingPriority the overriding binding priority for the registered contract(s).
     * @return the updated configurable instance.
     */
    public Configurable register(Class<?> providerClass, int bindingPriority);

    /**
     * Register a provider or a {@link Feature feature} class to be instantiated
     * and used in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Class)}
     * except the provider class is only registered as a provider of the listed
     * {@code contracts}. Note that in case the {@link Feature} interface is not listed
     * explicitly, the provider class is not recognized as a JAX-RS feature.
     * </p>
     *
     * @param providerClass provider class to be instantiated and used in the scope
     *                      of the configured instance.
     * @param contracts     the specific set of contracts implemented by the provider class
     *                      for which the provider should be registered. If omitted, the
     *                      provider class will be registered as a provider of all recognized
     *                      contracts implemented by the provider class.
     * @return the updated configurable instance.
     */
    public <T> Configurable register(Class<T> providerClass, Class<? super T>... contracts);

    /**
     * Register a provider or a {@link Feature feature} class to be instantiated
     * and used in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Class, Class[])}
     * except that any provider binding priority specified on the provider class using
     * {@link javax.ws.rs.BindingPriority &#64;BindingPriority} annotation is overridden
     * with the supplied {@code bindingPriority} value.
     * </p>
     * <p>
     * Note that in case the binding priority cannot be applied to a particular provider
     * contract registered for the provider class, the supplied {@code bindingPriority} value
     * is ignored.
     * </p>
     *
     * @param providerClass   provider class to be instantiated and used in the scope
     *                        of the configured instance.
     * @param bindingPriority the overriding binding priority for the registered contract(s).
     * @param contracts       the specific set of contracts implemented by the provider class
     *                        for which the provider should be registered. If omitted, the
     *                        provider class will be registered as a provider of all recognized
     *                        contracts implemented by the provider class.
     * @return the updated configurable instance.
     */
    public <T> Configurable register(Class<T> providerClass, int bindingPriority, Class<? super T>... contracts);

    /**
     * Register a provider or a {@link Feature feature} ("singleton") instance to be used
     * in the scope of the configured instance.
     *
     * The registered provider is registered as a provider of all the recognized JAX-RS
     * or implementation-specific extension contracts.
     * <p>
     * As opposed to the providers registered by the
     * {@link #register(Class) provider classes}, provider instances
     * registered using this method are used "as is, i.e. are not managed or
     * injected by the JAX-RS implementation provider. In case of a conflict
     * between a registered provider instance and instantiated registered provider
     * class, the registered provider instance takes precedence and the registered
     * provider class will not be instantiated in such case.
     * </p>
     * <p>
     * In case the registered class represents a {@link Feature feature},
     * this {@code Configurable} context instantiates the feature and invokes the
     * {@link Feature#configure(javax.ws.rs.core.Configurable)} method and
     * lets the feature update the configurable context. If the invocation
     * of {@link Feature#configure(javax.ws.rs.core.Configurable)} returns {@code true}
     * the feature is added to the {@link #getFeatures() collection of enabled features},
     * otherwise the feature instance is discarded.
     * </p>
     *
     * @param provider a provider instance to be registered in the scope of the configured
     *                 instance.
     * @return the updated configurable instance.
     */
    public Configurable register(Object provider);

    /**
     * Register a provider or a {@link Feature feature} ("singleton") instance to be used
     * in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Object)}
     * except that any provider binding priority specified on the provider using
     * {@link javax.ws.rs.BindingPriority &#64;BindingPriority} annotation is overridden
     * with the supplied {@code bindingPriority} value.
     * </p>
     * <p>
     * Note that in case the binding priority cannot be applied to a particular provider
     * contract registered for the provider, the supplied {@code bindingPriority} value
     * is ignored.
     * </p>
     *
     * @param provider        provider class to be instantiated and used in the scope
     *                        of the configured instance.
     * @param bindingPriority the overriding binding priority for the registered contract(s).
     * @return the updated configurable instance.
     */
    public Configurable register(Object provider, int bindingPriority);

    /**
     * Register a provider or a {@link Feature feature} ("singleton") instance to be used
     * in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Object)}
     * except the provider is only registered as a provider of the listed
     * {@code contracts}. Note that in case the {@link Feature} interface is not listed
     * explicitly, the provider is not recognized as a JAX-RS feature.
     * </p>
     *
     * @param provider  a provider instance to be registered in the scope of the configured
     *                  instance.
     * @param contracts the specific set of contracts implemented by the provider class
     *                  for which the provider should be registered. If omitted, the
     *                  provider class will be registered as a provider of all recognized
     *                  contracts implemented by the provider class.
     * @return the updated configurable instance.
     */
    public <T> Configurable register(Object provider, Class<? super T>... contracts);

    /**
     * Register a provider or a {@link Feature feature} ("singleton") instance to be used
     * in the scope of the configured instance.
     * <p>
     * This registration method provides same functionality as {@link #register(Object, Class[])}
     * except that any provider binding priority specified on the provider using
     * {@link javax.ws.rs.BindingPriority &#64;BindingPriority} annotation is overridden
     * with the supplied {@code bindingPriority} value.
     * </p>
     * <p>
     * Note that in case the binding priority cannot be applied to a particular provider
     * contract registered for the provider, the supplied {@code bindingPriority} value
     * is ignored.
     * </p>
     *
     * @param provider  a provider instance to be registered in the scope of the configured
     *                  instance.
     * @param bindingPriority the overriding binding priority for the registered contract(s).
     * @param contracts the specific set of contracts implemented by the provider class
     *                  for which the provider should be registered. If omitted, the
     *                  provider class will be registered as a provider of all recognized
     *                  contracts implemented by the provider class.
     * @return the updated configurable instance.
     */
    public <T> Configurable register(Object provider, int bindingPriority, Class<? super T>... contracts);
}
