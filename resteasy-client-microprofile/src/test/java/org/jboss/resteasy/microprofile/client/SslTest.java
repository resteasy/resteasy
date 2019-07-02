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
        RestClientBuilderImpl.setSslEnabled(false);
        Assert.assertFalse(RestClientBuilderImpl.SSL_ENABLED);
    }
}
