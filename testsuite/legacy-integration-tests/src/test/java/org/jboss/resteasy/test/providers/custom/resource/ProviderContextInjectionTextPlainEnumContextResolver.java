package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class ProviderContextInjectionTextPlainEnumContextResolver implements ContextResolver<ProviderContextInjectionEnumProvider> {
    @Override
    public ProviderContextInjectionEnumProvider getContext(Class<?> type) {
        return type == ProviderContextInjectionEnumProvider.class ? ProviderContextInjectionEnumProvider.CTS : null;
    }
}
