package org.jboss.resteasy.test.providers.custom.resource;

import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProviderContextInjectionEnumContextResolver implements ContextResolver<ProviderContextInjectionEnumProvider> {

    @Override
    public ProviderContextInjectionEnumProvider getContext(Class<?> type) {
        return type == ProviderContextInjectionEnumProvider.class ? ProviderContextInjectionEnumProvider.CTS : null;
    }

}
