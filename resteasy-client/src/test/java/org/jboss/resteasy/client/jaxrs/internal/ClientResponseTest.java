package org.jboss.resteasy.client.jaxrs.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.ResteasyContext.CloseableContext;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.StringTextStar;
import org.junit.jupiter.api.Test;

public class ClientResponseTest {

    @Test
    public void readEntityDoesNotModifyRequestContext() {
        Providers serverProviders = new ResteasyProviderFactoryImpl();
        Map<Class<?>, Object> requestContext = new HashMap<>();
        Configuration serverConfiguration = new ResteasyProviderFactoryImpl();
        requestContext.put(Providers.class, serverProviders);
        requestContext.put(Configuration.class, serverConfiguration);
        AtomicReference<Object> providersDuringRead = new AtomicReference<>();
        AtomicReference<Object> configurationDuringRead = new AtomicReference<>();
        AtomicReference<Object> requestProvidersDuringRead = new AtomicReference<>();
        AtomicReference<Object> requestConfigurationDuringRead = new AtomicReference<>();

        ClientConfiguration configuration = new ClientConfiguration(new LocalResteasyProviderFactory())
                .register(new StringTextStar());
        configuration.register((ReaderInterceptor) context -> {
            providersDuringRead.set(ResteasyContext.getContextData(Providers.class));
            configurationDuringRead.set(ResteasyContext.getContextData(Configuration.class));
            requestProvidersDuringRead.set(requestContext.get(Providers.class));
            requestConfigurationDuringRead.set(requestContext.get(Configuration.class));
            return context.proceed();
        });

        try (CloseableContext ignored = ResteasyContext.addCloseableContextDataLevel(requestContext);
                ClientResponse response = new AbortedResponse(configuration,
                        Response.ok("entity", MediaType.TEXT_PLAIN_TYPE).build())) {
            assertEquals("entity", response.readEntity(String.class));
            assertSame(requestContext, ResteasyContext.getContextDataMap(false));
        }

        assertSame(configuration, providersDuringRead.get());
        assertSame(configuration, configurationDuringRead.get());
        assertSame(serverProviders, requestProvidersDuringRead.get());
        assertSame(serverConfiguration, requestConfigurationDuringRead.get());
        assertEquals(Map.of(Providers.class, serverProviders, Configuration.class, serverConfiguration), requestContext);
    }
}
