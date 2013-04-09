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
package javax.ws.rs.core;

import java.util.Map;

/**
 * Represents a client or server-side configurable context in JAX-RS.
 *
 * A configurable context can be used to define the JAX-RS components as well as
 * additional meta-data that should be used in the scope of the configured context.
 * The modification of the context typically involves setting properties or registering
 * custom JAX-RS components, such as providers and/or features.
 * All modifications of a {@code Configurable} context are reflected in the associated
 * {@link Configuration} state which is exposed via {@link #getConfiguration()} method.
 * <p>
 * A configurable context can be either indirectly associated with a particular JAX-RS component
 * (such as application or resource method configurable context passed to a {@link Feature}
 * or {@link javax.ws.rs.container.DynamicFeature} meta-providers) or can be directly represented
 * by a concrete JAX-RS component implementing the {@code Configurable} interface
 * (such as {@link javax.ws.rs.client.Client} or {@link javax.ws.rs.client.WebTarget}).
 * As such, the exact scope of a configuration context is typically determined by a use case
 * scenario in which the context is accessed.
 * </p>
 * <h3>Setting properties.</h3>
 * <p>
 * New properties can be set using the {@link #property} method. Similarly, updating a value of
 * an existing property can be achieved using the same method. Information about the configured set of
 * properties is available via the underlying {@code Configuration} object. An existing property
 * can be removed by assigning a {@code null} value to the property.
 * </p>
 * <h3>Registering JAX-RS components.</h3>
 * <p>
 * Registered custom JAX-RS component classes and instances are important part of the contextual
 * configuration information as these are the main factors that determine the capabilities of
 * a configured runtime.
 * Implementations SHOULD warn about and ignore registrations that do not conform to the requirements
 * of supported JAX-RS components in a given configurable context.
 * </p>
 * <p>
 * In most cases, when registering a JAX-RS component, the simplest form of methods
 * ({@link #register(Class)} or {@link #register(Object)}) of the available registration API
 * is sufficient.
 * </p>
 * <p>
 * For example:
 * <pre>
 * config.register(HtmlDocumentReader.class);
 * config.register(new HtmlDocumentWriter(writerConfig));
 * </pre>
 * </p>
 * <p>
 * In some situations a JAX-RS component class or instance may implement multiple
 * provider contracts recognized by a JAX-RS implementation (e.g. filter, interceptor or entity provider).
 * By default, the JAX-RS implementation MUST register the new component class or instance as
 * a provider for all the recognized provider contracts implemented by the component class.
 * </p>
 * <p>
 * For example:
 * <pre>
 * &#64;Priority(ENTITY_CODER)
 * public class GzipInterceptor
 *         implements ReaderInterceptor, WriterInterceptor { ... }
 *
 * ...
 *
 * // register GzipInterceptor as a ReaderInterceptor
 * // as well as a WriterInterceptor
 * config.register(GzipInterceptor.class);
 * </pre>
 * </p>
 * <p>
 * There are however situations when the default registration of a JAX-RS component to all the
 * recognized provider contracts is not desirable. In such cases users may use other versions of the
 * {@code register(...)} method to explicitly specify the collection of the provider contracts
 * for which the JAX-RS component should be registered and/or the priority of each registered
 * provider contract.
 * </p>
 * <p>
 * For example:
 * <pre>
 * &#64;Priority(USER)
 * public class ClientLoggingFilter
 *         implements ClientRequestFilter, ClientResponseFilter { ... }
 *
 * &#64;Priority(ENTITY_CODER)
 * public class GzipInterceptor
 *         implements ReaderInterceptor, WriterInterceptor { ... }
 *
 * ...
 *
 * // register ClientLoggingFilter as a ClientResponseFilter only
 * config.register(ClientLoggingFilter.class, ClientResponseFilter.class);
 *
 * // override the priority of registered GzipInterceptor
 * // and both of it's provider contracts
 * config.register(GzipInterceptor.class, 6500);
 * </pre>
 * </p>
 * <p>
 * As a general rule, for each JAX-RS component class there can be at most one registration
 * &mdash; class-based or instance-based &mdash; configured at any given moment. Implementations
 * MUST reject any attempts to configure a new registration for a provider class that has been
 * already registered in the given configurable context earlier. Implementations SHOULD also raise
 * a warning to inform the user about the rejected component registration.
 * </p>
 * <p>
 * For example:
 * <pre>
 * config.register(GzipInterceptor.class, WriterInterceptor.class);
 * config.register(GzipInterceptor.class);       // Rejected by runtime.
 * config.register(new GzipInterceptor());       // Rejected by runtime.
 * config.register(GzipInterceptor.class, 6500); // Rejected by runtime.
 *
 * config.register(new ClientLoggingFilter());
 * config.register(ClientLoggingFilter.class);   // rejected by runtime.
 * config.register(ClientLoggingFilter.class,
 *                 ClientResponseFilter.class);  // Rejected by runtime.
 * </pre>
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Configurable<C extends Configurable> {

    /**
     * Get a live view of an internal configuration state of this configurable instance.
     *
     * Any changes made using methods of this {@code Configurable} instance will be reflected
     * in the returned {@code Configuration} instance.
     * <p>
     * The returned {@code Configuration} instance and the collection data it provides are not
     * thread-safe wrt. modification made using methods on the parent configurable object.
     * </p>
     *
     * @return configuration live view of the internal configuration state.
     */
    public Configuration getConfiguration();

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
    public C property(String name, Object value);

    /**
     * Register a class of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     *
     * Implementations SHOULD warn about and ignore registrations that do not
     * conform to the requirements of supported JAX-RS component types in the
     * given configurable context. Any subsequent registration attempts for a component
     * type, for which a class or instance-based registration already exists in the system
     * MUST be rejected by the JAX-RS implementation and a warning SHOULD be raised to
     * inform the user about the rejected registration.
     *
     * The registered JAX-RS component class is registered as a contract provider of
     * all the recognized JAX-RS or implementation-specific extension contracts including
     * meta-provider contracts, such as {@code Feature} or {@link javax.ws.rs.container.DynamicFeature}.
     * <p>
     * As opposed to component instances registered via {@link #register(Object)} method,
     * the lifecycle of components registered using this class-based {@code register(...)}
     * method is fully managed by the JAX-RS implementation or any underlying IoC
     * container supported by the implementation.
     * </p>
     *
     * @param componentClass JAX-RS component class to be configured in the scope of this
     *                       configurable context.
     * @return the updated configurable context.
     */
    public C register(Class<?> componentClass);

    /**
     * Register a class of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides the same functionality as {@link #register(Class)}
     * except that any priority specified on the registered JAX-RS component class via
     * {@code javax.annotation.Priority} annotation is overridden with the supplied
     * {@code priority} value.
     * </p>
     * <p>
     * Note that in case the priority is not applicable to a particular
     * provider contract implemented by the class of the registered component, the supplied
     * {@code priority} value will be ignored for that contract.
     * </p>
     *
     * @param componentClass JAX-RS component class to be configured in the scope of this
     *                       configurable context.
     * @param priority       the overriding priority for the registered component
     *                       and all the provider contracts the component implements.
     * @return the updated configurable context.
     */
    public C register(Class<?> componentClass, int priority);

    /**
     * Register a class of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides the same functionality as {@link #register(Class)}
     * except the JAX-RS component class is only registered as a provider of the listed
     * extension provider or meta-provider {@code contracts}.
     * All explicitly enumerated contract types must represent a class or an interface
     * implemented or extended by the registered component. Contracts that are not
     * {@link Class#isAssignableFrom(Class) assignable from} the registered component class
     * MUST be ignored and implementations SHOULD raise a warning to inform users about the
     * ignored contract(s).
     * </p>
     *
     * @param componentClass JAX-RS component class to be configured in the scope of this
     *                       configurable context.
     * @param contracts      the specific extension provider or meta-provider contracts
     *                       implemented by the component for which the component should
     *                       be registered.
     *                       Implementations MUST ignore attempts to register a component
     *                       class for an empty or {@code null} collection of contracts via
     *                       this method and SHOULD raise a warning about such event.
     * @return the updated configurable context.
     */
    public C register(Class<?> componentClass, Class<?>... contracts);

    /**
     * Register a class of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides same functionality as {@link #register(Class, Class[])}
     * except that any priority specified on the registered JAX-RS component class via
     * {@code javax.annotation.Priority} annotation is overridden
     * for each extension provider contract type separately with an integer priority value
     * specified as a value in the supplied map of [contract type, priority] pairs.
     * </p>
     * <p>
     * Note that in case a priority is not applicable to a provider contract registered
     * for the JAX-RS component, the supplied priority value is ignored for such
     * contract.
     * </p>
     *
     * @param componentClass JAX-RS component class to be configured in the scope of this
     *                       configurable context.
     * @param contracts      map of the specific extension provider and meta-provider contracts
     *                       and their associated priorities for which the JAX-RS component
     *                       is registered.
     *                       All contracts in the map must represent a class or an interface
     *                       implemented or extended by the JAX-RS component. Contracts that are
     *                       not {@link Class#isAssignableFrom(Class) assignable from} the registered
     *                       component class MUST be ignored and implementations SHOULD raise a warning
     *                       to inform users about the ignored contract(s).
     * @return the updated configurable context.
     */
    public C register(Class<?> componentClass, Map<Class<?>, Integer> contracts);

    /**
     * Register an instance of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     *
     * Implementations SHOULD warn about and ignore registrations that do not
     * conform to the requirements of supported JAX-RS component types in the
     * given configurable context. Any subsequent registration attempts for a component
     * type, for which a class or instance-based registration already exists in the system
     * MUST be rejected by the JAX-RS implementation and a warning SHOULD be raised to
     * inform the user about the rejected registration.
     *
     * The registered JAX-RS component is registered as a contract provider of
     * all the recognized JAX-RS or implementation-specific extension contracts including
     * meta-provider contracts, such as {@code Feature} or {@link javax.ws.rs.container.DynamicFeature}.
     * <p>
     * As opposed to components registered via {@link #register(Class)} method,
     * the lifecycle of providers registered using this instance-based {@code register(...)}
     * is not managed by JAX-RS runtime. The same registered component instance is used during
     * the whole lifespan of the configurable context.
     * Fields and properties of all registered JAX-RS component instances are injected with their
     * declared dependencies (see {@link Context}) by the JAX-RS runtime prior to use.
     * </p>
     *
     * @param component JAX-RS component instance to be configured in the scope of this
     *                  configurable context.
     * @return the updated configurable context.
     */
    public C register(Object component);

    /**
     * Register an instance of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides the same functionality as {@link #register(Object)}
     * except that any priority specified on the registered JAX-RS component class via
     * {@code javax.annotation.Priority} annotation is overridden with the supplied
     * {@code priority} value.
     * </p>
     * <p>
     * Note that in case the priority is not applicable to a particular
     * provider contract implemented by the class of the registered component, the supplied
     * {@code priority} value will be ignored for that contract.
     * </p>
     *
     * @param component JAX-RS component instance to be configured in the scope of this
     *                  configurable context.
     * @param priority  the overriding priority for the registered component
     *                  and all the provider contracts the component implements.
     * @return the updated configurable context.
     */
    public C register(Object component, int priority);

    /**
     * Register an instance of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides the same functionality as {@link #register(Object)}
     * except the JAX-RS component class is only registered as a provider of the listed
     * extension provider or meta-provider {@code contracts}.
     * All explicitly enumerated contract types must represent a class or an interface
     * implemented or extended by the registered component. Contracts that are not
     * {@link Class#isAssignableFrom(Class) assignable from} the registered component class
     * MUST be ignored and implementations SHOULD raise a warning to inform users about the
     * ignored contract(s).
     * </p>
     *
     * @param component JAX-RS component instance to be configured in the scope of this
     *                  configurable context.
     * @param contracts the specific extension provider or meta-provider contracts
     *                  implemented by the component for which the component should
     *                  be registered.
     *                  Implementations MUST ignore attempts to register a component
     *                  class for an empty or {@code null} collection of contracts via
     *                  this method and SHOULD raise a warning about such event.
     * @return the updated configurable context.
     */
    public C register(Object component, Class<?>... contracts);

    /**
     * Register an instance of a custom JAX-RS component (such as an extension provider or
     * a {@link javax.ws.rs.core.Feature feature} meta-provider) to be instantiated
     * and used in the scope of this configurable context.
     * <p>
     * This registration method provides same functionality as {@link #register(Object, Class[])}
     * except that any priority specified on the registered JAX-RS component class via
     * {@code javax.annotation.Priority} annotation is overridden
     * for each extension provider contract type separately with an integer priority value
     * specified as a value in the supplied map of [contract type, priority] pairs.
     * </p>
     * <p>
     * Note that in case a priority is not applicable to a provider contract registered
     * for the JAX-RS component, the supplied priority value is ignored for such
     * contract.
     * </p>
     *
     * @param component JAX-RS component instance to be configured in the scope of this
     *                  configurable context.
     * @param contracts map of the specific extension provider and meta-provider contracts
     *                  and their associated priorities for which the JAX-RS component
     *                  is registered.
     *                  All contracts in the map must represent a class or an interface
     *                  implemented or extended by the JAX-RS component. Contracts that are
     *                  not {@link Class#isAssignableFrom(Class) assignable from} the registered
     *                  component class MUST be ignored and implementations SHOULD raise a warning
     *                  to inform users about the ignored contract(s).
     * @return the updated configurable context.
     */
    public C register(Object component, Map<Class<?>, Integer> contracts);
}
