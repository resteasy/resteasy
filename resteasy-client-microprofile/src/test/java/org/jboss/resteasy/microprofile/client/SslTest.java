package org.jboss.resteasy.microprofile.client;

import org.junit.Assert;
import org.junit.Test;

public class SslTest {

    @Test
    public void testEnabled() {
        Assert.assertTrue(RestClientBuilderImpl.SSL_ENABLED);
    }

    @Test
    public void testDisabled() {
        try {
            RestClientBuilderImpl.setSslEnabled(false);
            Assert.assertFalse(RestClientBuilderImpl.SSL_ENABLED);
        } finally {
            // Reset to default state
            RestClientBuilderImpl.setSslEnabled(true);
        }
    }
}
