package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resteasy-client proxy
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.20
 *
 */
public class ProxyEqualsTest {

    public interface I {

    }

    /**
     * @tpTestDetails Verify equality works for proxies
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void proxyEquals() throws Exception {
        Client client = ClientBuilder.newClient();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL("/"));
        I proxy1 = target.proxy(I.class);
        I proxy2 = target.proxy(I.class);
        Assertions.assertTrue(proxy1.equals(proxy1), "proxy1 == proxy1");
        Assertions.assertFalse(proxy1.equals(proxy2), "proxy1 != proxy2");
        Assertions.assertTrue(proxy1.hashCode() == proxy1.hashCode());
        client.close();
    }
}
