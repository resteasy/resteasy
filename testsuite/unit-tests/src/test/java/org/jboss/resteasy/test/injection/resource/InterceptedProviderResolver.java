package org.jboss.resteasy.test.injection.resource;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ContextResolver;

/**
 * An intercepted {@link Singleton} bean, which Weld creates as a subclass rather than a client proxy.
 */
@Singleton
@Priority(100)
@InterceptedProviderBinding
public class InterceptedProviderResolver implements ContextResolver<String> {

    @Override
    public String getContext(final Class<?> type) {
        return "intercepted";
    }
}
