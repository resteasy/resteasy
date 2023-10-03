package org.jboss.resteasy.test.other;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DisableAutomaticRetriesTest {
    @Test
    public void testFlag() throws Exception {
        ResteasyClientBuilder builder = (ResteasyClientBuilder) ClientBuilder.newBuilder();
        Assertions.assertFalse(builder.isDisableAutomaticRetries());
        builder.disableAutomaticRetries();
        Client client = builder.build();
        Assertions.assertTrue(builder.isDisableAutomaticRetries());
        client.close();
    }
}
