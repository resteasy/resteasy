package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.DefaultTextPlain;
import org.jboss.resteasy.plugins.providers.IIOImageProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Assert;
import org.junit.Test;

public class ProviderFactoryTest {

    @Test
    public void testDefaultProvider() {
        RestClientBuilderImpl builder = (RestClientBuilderImpl) RestClientBuilder.newBuilder();

        Assert.assertTrue(builder.getBuilderDelegate().getProviderFactory().getProviderClasses().contains(IIOImageProvider.class));
        Assert.assertTrue(builder.getBuilderDelegate().getProviderFactory().getProviderClasses().contains(DefaultTextPlain.class));
    }

    @Test
    public void testCustomProvider() {
        try {
            ResteasyProviderFactory provider = new ResteasyProviderFactoryImpl(null, true);
            provider.registerProvider(IIOImageProvider.class);
            RestClientBuilderImpl.setProviderFactory(provider);

            RestClientBuilderImpl builder = (RestClientBuilderImpl) RestClientBuilder.newBuilder();

            Assert.assertTrue(builder.getBuilderDelegate().getProviderFactory().getProviderClasses().contains(IIOImageProvider.class));
            Assert.assertFalse(builder.getBuilderDelegate().getProviderFactory().getProviderClasses().contains(DefaultTextPlain.class));
        } finally {
            // Reset to default state
            RestClientBuilderImpl.setProviderFactory(null);
        }
    }
}
