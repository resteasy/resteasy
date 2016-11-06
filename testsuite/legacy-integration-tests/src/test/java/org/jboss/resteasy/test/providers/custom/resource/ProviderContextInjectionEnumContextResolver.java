package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class ProviderContextInjectionEnumContextResolver implements ContextResolver<ProviderContextInjectionEnumProvider> {

    @Override
    public ProviderContextInjectionEnumProvider getContext(Class<?> type) {
        return type == ProviderContextInjectionEnumProvider.class ? ProviderContextInjectionEnumProvider.JAXRS : null;
    }

}
