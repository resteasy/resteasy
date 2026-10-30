/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.test.interceptor;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;

/**
 * Tests that a {@link Feature} or {@link DynamicFeature} loaded with {@link java.util.ServiceLoader} is registered
 * only as the loaded instance, even if its class is a CDI bean or is also discovered through {@link Provider}.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@RequiresModule(value = "org.jboss.resteasy.resteasy-cdi", minVersion = "8.0.0.Alpha1")
public class ServiceLoadedFeatureTest {
    private static final String SERVICE_LOADER = "service-loader";
    private static final String CDI = "cdi";

    @ArquillianResource
    public URL url;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, ServiceLoadedFeatureTest.class.getSimpleName() + ".war")
                .addClasses(
                        ConfiguredResource.class,
                        ServicesOnlyFeature.class,
                        ProviderFeature.class,
                        ProviderDynamicFeature.class,
                        TestApplication.class)
                .addAsServiceProvider(Feature.class, ServicesOnlyFeature.class, ProviderFeature.class)
                .addAsServiceProvider(DynamicFeature.class, ProviderDynamicFeature.class);
    }

    /**
     * A service-loaded feature whose class is a CDI bean is registered as the loaded instance.
     */
    @Test
    public void servicesOnly() throws Exception {
        assertConfigured(ServicesOnlyFeature.class);
    }

    /**
     * A service-loaded feature which is also annotated with {@link Provider} is registered once, as the loaded
     * instance.
     */
    @Test
    public void servicesAndProvider() throws Exception {
        assertConfigured(ProviderFeature.class);
    }

    /**
     * A service-loaded dynamic feature which is also annotated with {@link Provider} is registered once, as the loaded
     * instance. The resource has a single method, so the dynamic feature is configured once.
     */
    @Test
    public void dynamicFeatureServicesAndProvider() throws Exception {
        assertConfigured(ProviderDynamicFeature.class);
    }

    private void assertConfigured(final Class<?> featureType) throws Exception {
        try (
                Client client = ClientBuilder.newClient();
                Response response = client.target(TestUtil.generateUri(url, "/configured/%s"
                        .formatted(featureType.getSimpleName())))
                        .request(MediaType.TEXT_PLAIN)
                        .get()) {
            final String entity = response.readEntity(String.class);
            Assertions.assertEquals(200, response.getStatus(), entity);
            Assertions.assertEquals(SERVICE_LOADER, entity,
                    () -> "Expected %s to be configured once by the instance loaded with the ServiceLoader"
                            .formatted(featureType.getSimpleName()));
        }
    }

    @ApplicationScoped
    public static class ServicesOnlyFeature implements Feature {
        private final String loadedBy;

        public ServicesOnlyFeature() {
            this.loadedBy = SERVICE_LOADER;
        }

        @Inject
        public ServicesOnlyFeature(final BeanManager beanManager) {
            this.loadedBy = CDI;
        }

        @Override
        public boolean configure(final FeatureContext context) {
            ConfiguredResource.configured(ServicesOnlyFeature.class, loadedBy);
            return true;
        }
    }

    @Provider
    @ApplicationScoped
    public static class ProviderFeature implements Feature {
        private final String loadedBy;

        public ProviderFeature() {
            this.loadedBy = SERVICE_LOADER;
        }

        @Inject
        public ProviderFeature(final BeanManager beanManager) {
            this.loadedBy = CDI;
        }

        @Override
        public boolean configure(final FeatureContext context) {
            ConfiguredResource.configured(ProviderFeature.class, loadedBy);
            return true;
        }
    }

    @Provider
    @ApplicationScoped
    public static class ProviderDynamicFeature implements DynamicFeature {
        private final String loadedBy;

        public ProviderDynamicFeature() {
            this.loadedBy = SERVICE_LOADER;
        }

        @Inject
        public ProviderDynamicFeature(final BeanManager beanManager) {
            this.loadedBy = CDI;
        }

        @Override
        public void configure(final ResourceInfo resourceInfo, final FeatureContext context) {
            ConfiguredResource.configured(ProviderDynamicFeature.class, loadedBy);
        }
    }

    @Path("/configured")
    public static class ConfiguredResource {
        static final Map<String, List<String>> CONFIGURED = new ConcurrentHashMap<>();

        @GET
        @Produces(MediaType.TEXT_PLAIN)
        @Path("/{name}")
        public String configured(@PathParam("name") final String name) {
            return String.join(",", CONFIGURED.getOrDefault(name, List.of()));
        }

        /**
         * Records which instance of a feature was configured. Each entry is how the configured instance was created.
         */
        static void configured(final Class<?> featureType, final String loadedBy) {
            CONFIGURED.computeIfAbsent(featureType.getSimpleName(), (key) -> new CopyOnWriteArrayList<>())
                    .add(loadedBy);
        }
    }

    @ApplicationPath("/")
    public static class TestApplication extends Application {
    }
}
