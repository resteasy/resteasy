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

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.cdi.resources.ContextAndInjectionFilter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

/**
 * Tests that a {@link Feature} can inject {@link jakarta.ws.rs.core.UriInfo} via
 * {@link jakarta.inject.Inject @Inject} and in a CDI bean. The feature is registered as a provider with via a
 * {@link java.util.ServiceLoader service}.
 *
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FeatureServiceContextInjectionTest extends AbstractRegistrationTest {

    @Deployment
    public static WebArchive deployment() {
        return createDeployment(FeatureServiceContextInjectionTest.class)
                .addClasses(
                        ContextAndInjectionFilter.class,
                        ContextAndInjectionFilterFeature.class)
                .addAsServiceProvider(Feature.class, ContextAndInjectionFilterFeature.class);
    }

    public static class ContextAndInjectionFilterFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            context.register(ContextAndInjectionFilter.class);
            return true;
        }
    }
}
