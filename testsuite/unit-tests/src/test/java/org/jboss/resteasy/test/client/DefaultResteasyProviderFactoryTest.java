package org.jboss.resteasy.test.client;

import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Client
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for RESTEASY-2684
 * @tpSince RESTEasy 4.6
 */
public class DefaultResteasyProviderFactoryTest {

    @Test
    public void testDefaultResteasyProviderFactory() {
        ResteasyProviderFactory rpf = new ResteasyProviderFactoryImpl();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", "bar");
        rpf.setProperties(map);
        ResteasyClientBuilderImpl.setProviderFactory(rpf);
        Client client = ResteasyClientBuilderImpl.newClient();
        Assertions.assertEquals("bar", client.getConfiguration().getProperty("foo"));
        client.close();
        ResteasyClientBuilderImpl.setProviderFactory(null);
    }

}
