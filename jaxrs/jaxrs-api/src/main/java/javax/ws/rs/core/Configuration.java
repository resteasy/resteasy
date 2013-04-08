/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
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

import javax.ws.rs.RuntimeType;

/**
 * A configuration state associated with a {@link Configurable configurable} JAX-RS context.
 * Defines the components as well as additional meta-data for the configured context.
 * <p>
 * A configuration state may be used to retrieve configuration information about
 * of the associated JAX-RS context (e.g. application, resource method, etc.) or component
 * (e.g. {@link javax.ws.rs.client.Client}, {@link javax.ws.rs.client.WebTarget}, etc.).
 * Configuration information consists of properties, registered JAX-RS component classes
 * and/or instances.
 * </p>
 * <p>
 * This interface can be injected using the {@link Context} annotation.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Configuration {

    /**
     * Get the runtime type of this configuration context.
     *
     * @return configuration context runtime type.
     */
    public RuntimeType getRuntimeType();

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
     * Returns an immutable {@link java.util.Collection collection} containing the
     * property names available within the context of the current configuration instance.
     * <p>
     * Use the {@link #getProperty} method with a property name to get the value of
     * a property.
     * </p>
     *
     * @return an immutable {@link java.util.Collection collection} of property names.
     * @see #getProperty
     */
    public Collection<String> getPropertyNames();

    /**
     * Check if a particular {@link Feature feature} instance has been previously
     * enabled in the runtime configuration context.
     * <p>
     * Method returns {@code true} only in case an instance equal to the {@code feature}
     * instance is already present among the features previously successfully enabled in
     * the configuration context.
     * </p>
     *
     * @param feature a feature instance to test for.
     * @return {@code true} if the feature instance has been previously enabled in this
     *         configuration context, {@code false} otherwise.
     */
    public boolean isEnabled(Feature feature);

    /**
     * Check if a {@link Feature feature} instance of {@code featureClass} class has been
     * previously enabled in the runtime configuration context.
     * <p>
     * Method returns {@code true} in case any instance of the {@code featureClass} class is
     * already present among the features previously successfully enabled in the configuration
     * context.
     * </p>
     *
     * @param featureClass a feature class to test for.
     * @return {@code true} if a feature of a given class has been previously enabled in this
     *         configuration context, {@code false} otherwise.
     */
    public boolean isEnabled(Class<? extends Feature> featureClass);

    /**
     * Check if a particular JAX-RS {@code component} instance (such as providers or
     * {@link Feature features}) has been previously registered in the runtime configuration context.
     * <p>
     * Method returns {@code true} only in case an instance equal to the {@code component}
     * instance is already present among the components previously registered in the configuration
     * context.
     * </p>
     *
     * @param component a component instance to test for.
     * @return {@code true} if the component instance has been previously registered in this
     *         configuration context, {@code false} otherwise.
     * @see #isEnabled(Feature)
     */
    public boolean isRegistered(Object component);

    /**
     * Check if a JAX-RS component of the supplied {@code componentClass} class has been previously
     * registered in the runtime configuration context.
     * <p>
     * Method returns {@code true} in case a component of the supplied {@code componentClass} class
     * is already present among the previously registered component classes or instances
     * in the configuration context.
     * </p>
     *
     * @param componentClass a component class to test for.
     * @return {@code true} if a component of a given class has been previously registered in this
     *         configuration context, {@code false} otherwise.
     * @see #isEnabled(Class)
     */
    public boolean isRegistered(Class<?> componentClass);

    /**
     * Get the extension contract registration information for a component of a given class.
     *
     * For component classes that are not configured in this configuration context the method returns
     * an empty {@code Map}. Method does not return {@code null}.
     *
     * @return map of extension contracts and their priorities for which the component class
     *         is registered.
     *         May return an empty map in case the component has not been registered for any
     *         extension contract supported by the implementation.
     */
    public Map<Class<?>, Integer> getContracts(Class<?> componentClass);

    /**
     * Get the immutable set of registered JAX-RS component (such as provider or
     * {@link Feature feature}) classes to be instantiated, injected and utilized in the scope
     * of the configurable instance.
     * <p>
     * For each component type, there can be only a single class-based or instance-based registration
     * present in the configuration context at any given time.
     * </p>
     *
     * @return the immutable set of registered JAX-RS component classes. The returned
     *         value may be empty but will never be {@code null}.
     * @see #getInstances
     */
    public Set<Class<?>> getClasses();

    /**
     * Get the immutable set of registered JAX-RS component (such as provider or
     * {@link Feature feature}) instances to be utilized by the configurable instance.
     * Fields and properties of returned instances are injected with their declared dependencies
     * (see {@link Context}) by the runtime prior to use.
     * <p>
     * For each component type, there can be only a single class-based or instance-based registration
     * present in the configuration context at any given time.
     * </p>
     *
     * @return the immutable set of registered JAX-RS component instances. The returned
     *         value may be empty but will never be {@code null}.
     * @see #getClasses
     */
    public Set<Object> getInstances();
}
