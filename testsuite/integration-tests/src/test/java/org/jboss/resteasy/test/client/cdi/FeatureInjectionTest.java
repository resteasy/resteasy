/*
 * JBoss, Home of Professional Open Source.
 *
 * Copyright 2022 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.resteasy.test.client.cdi;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.cdi.resources.InjectionFilter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests that a {@link Feature} can inject {@link jakarta.ws.rs.core.UriInfo} via
 * {@link jakarta.inject.Inject @Inject} and in a CDI bean. The feature is registered as a provider with
 * {@link Provider @Provider}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FeatureInjectionTest extends AbstractRegistrationTest {

    @Deployment
    public static WebArchive deployment() {
        return createDeployment(FeatureInjectionTest.class)
                .addClasses(
                        InjectionFilterFeature.class,
                        InjectionFilter.class);
    }

    @Provider
    public static class InjectionFilterFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            context.register(InjectionFilter.class);
            return true;
        }
    }
}
