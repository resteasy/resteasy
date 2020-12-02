package org.jboss.resteasy.test.other;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class DisableAutomaticRetriesTest {
    @Test
    public void testFlag() throws Exception {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        Assert.assertFalse(builder.isDisableAutomaticRetries());
        builder.disableAutomaticRetries();
        Client client = builder.build();
        Assert.assertTrue(builder.isDisableAutomaticRetries());
        client.close();
    }
}
