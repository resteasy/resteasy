package org.jboss.resteasy.test.injection;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.injection.resource.InterceptedProviderDefaultResolver;
import org.jboss.resteasy.test.injection.resource.InterceptedProviderInterceptor;
import org.jboss.resteasy.test.injection.resource.InterceptedProviderResolver;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that a provider instance, which is a CDI intercepted subclass, is registered with the annotations of the bean
 * class.
 *
 * @since 8.0
 */
public class InterceptedProviderTest {

    /**
     * The {@code @Priority} of an intercepted bean class is used for the subclass instance.
     */
    @Test
    public void interceptedProviderPriority() {
        try (
                WeldContainer container = new Weld()
                        .disableDiscovery()
                        .addBeanClasses(InterceptedProviderResolver.class, InterceptedProviderInterceptor.class)
                        .initialize()) {
            final InterceptedProviderResolver resolver = container.select(InterceptedProviderResolver.class).get();
            Assertions.assertNotEquals(InterceptedProviderResolver.class, resolver.getClass(),
                    "Expected the instance to be an intercepted subclass");

            final ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
            factory.registerProviderInstance(new InterceptedProviderDefaultResolver());
            factory.registerProviderInstance(resolver);

            final ContextResolver<String> contextResolver = factory.getContextResolver(String.class,
                    MediaType.WILDCARD_TYPE);
            Assertions.assertNotNull(contextResolver, "No ContextResolver found for String");
            Assertions.assertEquals("intercepted", contextResolver.getContext(Object.class));
        }
    }
}
