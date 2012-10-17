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

import java.util.Map;

import javax.ws.rs.core.Configurable;

/**
 * Represents inheritable configuration of the main client-side JAX-RS components,
 * such as {@link Client}, {@link WebTarget}, {@link Invocation.Builder Invocation Builder}
 * or {@link Invocation}.
 * <p>
 * Configuration is inherited from a parent component to a child component.
 * When creating new {@link WebTarget resource targets} using a {@link Client} instance,
 * the configuration of the {@code Client} instance is inherited by the child target
 * instances being created. Similarly, when creating new
 * {@code Invocation.Builder invocation builders} or derived resource targets
 * using a parent target instance, the configuration of the parent target is
 * inherited by the child instances being created.
 * <p>
 * </p>
 * The inherited configuration on a child instance reflects the state of the parent
 * configuration at the time of the child instance creation. Once the child instance
 * is created its configuration is detached from the parent configuration. This means
 * that any subsequent changes in the parent configuration do not affect
 * the configuration of previously created child instances.
 * <p>
 * </p>
 * Once the child instance is created, it's configuration can be further customized
 * using the provided set of instance configuration mutator methods. A change
 * made in the configuration of a child instance does not affect the configuration
 * of its parent, for example:
 * <pre>
 * Client client = ClientFactory.newClient();
 * client.configuration().setProperty("FOO_PROPERTY", "FOO_VALUE");
 *
 * // inherits the configured "FOO_PROPERTY" from the client instance
 * WebTarget resourceTarget = client.target("http://examples.jaxrs.com/");
 *
 * // does not modify the client instance configuration
 * resourceTarget.configuration().register(new BarFeature());
 * </pre>
 * </p>
 * <p>
 * For a discussion on registering providers or narrowing down the scope of the
 * contracts registered for each provider, see {@link javax.ws.rs.core.Configurable
 * configurable context documentation}.
 * </p>
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface Configuration extends Configurable {

    @Override
    public Configuration setProperties(Map<String, ?> properties);

    @Override
    public Configuration setProperty(String name, Object value);

    /**
     * Replace the existing configuration state with the configuration state of
     * the externally provided configuration.
     *
     * @param configuration configuration to be used to update the instance.
     * @return the updated configuration.
     */
    public Configuration updateFrom(Configurable configuration);

    @Override
    public Configuration register(Class<?> providerClass);

    @Override
    public Configuration register(Class<?> providerClass, int bindingPriority);

    @Override
    public <T> Configuration register(Class<T> providerClass, Class<? super T>... contracts);

    @Override
    public <T> Configuration register(Class<T> providerClass, int bindingPriority, Class<? super T>... contracts);

    @Override
    public Configuration register(Object provider);

    @Override
    public Configuration register(Object provider, int bindingPriority);

    @Override
    public <T> Configuration register(Object provider, Class<? super T>... contracts);

    @Override
    public <T> Configuration register(Object provider, int bindingPriority, Class<? super T>... contracts);
}
