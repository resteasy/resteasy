package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class ProviderContextInjectionTextPlainEnumContextResolver implements ContextResolver<ProviderContextInjectionEnumProvider> {
    @Override
    public ProviderContextInjectionEnumProvider getContext(Class<?> type) {
        return type == ProviderContextInjectionEnumProvider.class ? ProviderContextInjectionEnumProvider.JAXRS : null;
    }
}
