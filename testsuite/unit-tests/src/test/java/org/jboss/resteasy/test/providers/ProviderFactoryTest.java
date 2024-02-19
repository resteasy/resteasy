package org.jboss.resteasy.test.providers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.interception.JaxrsInterceptorRegistry;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryStrParamUnmarshaller;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Regression test for RESTEASY-584
 * @tpSince RESTEasy 3.0.16
 */
public class ProviderFactoryTest {

    private ResteasyProviderFactory factory;

    @BeforeEach
    public void createBean() {
        factory = ResteasyProviderFactory.newInstance();
    }

    /**
     * @tpTestDetails Basic check for ResteasyProviderFactory class.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldReturnStringParameterUnmarshallerAddedForType() {
        factory.registerProvider(ProviderFactoryStrParamUnmarshaller.class);
        assertNotNull(factory.createStringParameterUnmarshaller(Date.class),
                "Null StringParameterUnmarshaller object");
    }

    /**
     * @tpTestDetails Regression test for JBEAP-4706
     *                Test whether the priority is supplied to the container request filter registry.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRegisterProviderInstancePriorityContainerRequestFilter() throws Exception {
        ContainerRequestFilter requestFilter = new ContainerRequestFilter() {
            public void filter(ContainerRequestContext requestContext) {
            }
        };
        this.testRegisterProviderInstancePriority(requestFilter, factory.getContainerRequestFilterRegistry());
    }

    /**
     * @tpTestDetails Regression test for JBEAP-4706
     *                Test whether the priority is supplied to the container response filter registry.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testRegisterProviderInstancePriorityContainerResponseFilter() throws Exception {
        ContainerResponseFilter responseFilter = new ContainerResponseFilter() {
            public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
            }
        };
        this.testRegisterProviderInstancePriority(responseFilter, factory.getContainerResponseFilterRegistry());
    }

    /**
     * Generic helper method for RESTEASY-1311 cases, because the test logic is the same.
     * Unfortunately, there seems to be no public accessors for the properties we need,
     * so we have to resort to using reflection to check the right priority setting.
     */
    private void testRegisterProviderInstancePriority(Object filter, Object registry) throws Exception {
        int priorityOverride = Priorities.USER + 1;
        factory.registerProviderInstance(filter, null, priorityOverride, false);

        Field interceptorsField = registry.getClass().getSuperclass().getDeclaredField("interceptors");
        interceptorsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<JaxrsInterceptorRegistry.InterceptorFactory> interceptors = (List<JaxrsInterceptorRegistry.InterceptorFactory>) interceptorsField
                .get(registry);

        Field orderField = interceptors.get(0).getClass().getSuperclass().getDeclaredField("order");
        orderField.setAccessible(true);
        int order = (Integer) orderField.get(interceptors.get(0));
        Assertions.assertEquals(priorityOverride, order);
    }

    @Test
    public void testDeploymentStart() {
        ResteasyProviderFactory orig = ResteasyProviderFactory.peekInstance();
        try {
            ResteasyProviderFactory.setInstance(null);

            ResteasyProviderFactory rpf1 = ResteasyProviderFactory.newInstance();
            RegisterBuiltin.register(rpf1);
            rpf1.registerProvider(MyInterceptor.class);
            ResteasyDeployment dep1 = new ResteasyDeploymentImpl();
            dep1.setProviderFactory(rpf1);
            dep1.setDeploymentSensitiveFactoryEnabled(true);
            dep1.start();

            ResteasyProviderFactory rpf2 = ResteasyProviderFactory.newInstance();
            RegisterBuiltin.register(rpf2);
            rpf2.register(new DynamicFeature() {
                @Override
                public void configure(ResourceInfo resourceInfo, FeatureContext context) {
                    if (ResteasyProviderFactory.getInstance().isRegistered(MyInterceptor.class)) {
                        Assertions.fail("Second deployment consuming provider factory from first deployment");
                    }

                }
            });
            ResteasyDeployment dep2 = new ResteasyDeploymentImpl();
            dep2.setProviderFactory(rpf2);
            dep2.setDeploymentSensitiveFactoryEnabled(true);
            dep2.start();

            dep1.stop();
            dep2.stop();
        } finally {
            ResteasyProviderFactory.setInstance(orig);
        }
    }

    @Test
    public void testProviderFactoryAsJaxRsConfiguration() {
        ResteasyProviderFactory emptyResteasyProviderFactory = ResteasyProviderFactory.newInstance();
        ResteasyProviderFactory resteasyProviderFactory = ResteasyProviderFactory.newInstance();
        resteasyProviderFactory.register(new MyFeature());

        // test that getClasses() behavior is spec compliant
        Set<Class<?>> emptyProviderFactoryClasses = emptyResteasyProviderFactory.getClasses();
        Assertions.assertFalse(emptyProviderFactoryClasses == null, "Configuration.getClasses() MUST never be null.");
        Assertions.assertTrue(emptyProviderFactoryClasses.isEmpty(), "Configuration.getClasses() MUST be empty.");
        try {
            emptyProviderFactoryClasses.add(Object.class);
            Assertions.fail("Configuration.getClasses() MUST return an immutable set");
        } catch (UnsupportedOperationException e) {
        }

        Set<Class<?>> providerFactoryClasses = resteasyProviderFactory.getClasses();
        Assertions.assertTrue(providerFactoryClasses.size() == 1);
        Assertions.assertTrue(providerFactoryClasses.contains(MyInterceptor.class));
        try {
            providerFactoryClasses.add(Object.class);
            Assertions.fail("Configuration.getClasses() MUST return an immutable set");
        } catch (UnsupportedOperationException e) {
        }

        // test that getInstances() behavior is spec compliant
        Set<Object> emptyProviderFactoryInstances = emptyResteasyProviderFactory.getInstances();
        Assertions.assertFalse(emptyProviderFactoryInstances == null, "Configuration.getInstances() MUST never be null.");
        Assertions.assertTrue(emptyProviderFactoryInstances.isEmpty(), "Configuration.getInstances() MUST be empty.");
        try {
            emptyProviderFactoryInstances.add(new Object());
            Assertions.fail("Configuration.getInstances() MUST return an immutable set");
        } catch (UnsupportedOperationException e) {
        }

        Set<Object> providerFactoryInstances = resteasyProviderFactory.getInstances();
        Assertions.assertTrue(providerFactoryInstances.size() == 2);
        Assertions.assertTrue(providerFactoryInstances.contains(new MyFeature()));
        try {
            providerFactoryInstances.add(new Object());
            Assertions.fail("Configuration.getInstances() MUST return an immutable set");
        } catch (UnsupportedOperationException e) {
        }

        // test that isEnabled(Feature feature) behavior is spec compliant
        Assertions.assertFalse(emptyResteasyProviderFactory.isEnabled(new MyFeature()));
        Assertions.assertTrue(resteasyProviderFactory.isEnabled(new MyFeature()));

        // test that isEnabled(Class<Feature> featureClass) behavior is spec compliant
        Assertions.assertFalse(emptyResteasyProviderFactory.isEnabled(MyFeature.class));
        Assertions.assertTrue(resteasyProviderFactory.isEnabled(MyFeature.class));
    }

    @Provider
    public static class MyFeature implements Feature {
        @Override
        public boolean configure(FeatureContext featureContext) {
            featureContext.register(MyInterceptor.class);
            featureContext.register(new ContainerRequestFilter() {
                @Override
                public void filter(ContainerRequestContext arg0) throws IOException {
                }
            });
            return true;
        }

        @Override
        public int hashCode() {
            return MyFeature.class.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof MyFeature;
        }

    }

    @Provider
    public static class MyInterceptor implements ReaderInterceptor {
        @Override
        public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
