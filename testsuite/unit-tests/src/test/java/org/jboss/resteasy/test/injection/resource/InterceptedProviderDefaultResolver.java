package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.ext.ContextResolver;

public class InterceptedProviderDefaultResolver implements ContextResolver<String> {

    @Override
    public String getContext(final Class<?> type) {
        return "default";
    }
}
