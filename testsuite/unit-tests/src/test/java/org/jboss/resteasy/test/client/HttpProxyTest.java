package org.jboss.resteasy.test.client;

import jakarta.ws.rs.client.ClientBuilder;

import org.apache.http.HttpHost;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.ClientHttpEngineBuilder43;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter HTTP proxy setup
 * @tpSince RESTEasy 3.8.0
 */
// TODO (jrp) we should find a way to test this with the HttpClientEngine too
public class HttpProxyTest {

    @Test
    public void testHttpProxyHostSetup() {
        final String testProxyHost = "myproxy.com";
        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) createBuilder()
                .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, testProxyHost);
        ResteasyClient client = clientBuilder.build();
        ApacheHttpClient43Engine engine = (ApacheHttpClient43Engine) client.httpEngine();
        HttpHost proxy = engine.getDefaultProxy();

        Assert.assertEquals(testProxyHost, proxy.getHostName());
        // since port was not set, it must be -1
        Assert.assertEquals(-1, proxy.getPort());
        // since scheme was not set, it must be http
        Assert.assertEquals("http", proxy.getSchemeName());
        client.close();
    }

    @Test
    public void testHttpProxyHostPortSchemeSetup() {
        final String testProxyHost = "myproxy.com";
        final String testProxyPort = "8080";
        final String testProxyScheme = "https";
        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) createBuilder()
                .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, testProxyHost)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, testProxyPort)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_SCHEME, testProxyScheme);
        ResteasyClient client = clientBuilder.build();
        ApacheHttpClient43Engine engine = (ApacheHttpClient43Engine) client.httpEngine();
        HttpHost proxy = engine.getDefaultProxy();

        Assert.assertEquals(testProxyHost, proxy.getHostName());
        Assert.assertEquals(Integer.parseInt(testProxyPort), proxy.getPort());
        Assert.assertEquals(testProxyScheme, proxy.getSchemeName());
        client.close();

        //modify and re-use builder...
        clientBuilder.property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, Integer.parseInt(testProxyPort) + 10)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_SCHEME, "http");
        client = clientBuilder.build();
        engine = (ApacheHttpClient43Engine) client.httpEngine();
        proxy = engine.getDefaultProxy();

        Assert.assertEquals(testProxyHost, proxy.getHostName());
        Assert.assertEquals(8090, proxy.getPort());
        Assert.assertEquals("http", proxy.getSchemeName());
        client.close();
    }

    @Test
    public void testHttpProxyOverride() {
        ResteasyClientBuilder clientBuilder = (ResteasyClientBuilder) createBuilder()
                .property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, "myproxy.com")
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, "8080")
                .property(ResteasyClientBuilder.PROPERTY_PROXY_SCHEME, "https");
        clientBuilder.defaultProxy("myoverrideproxy.com");
        ResteasyClient client = clientBuilder.build();
        ApacheHttpClient43Engine engine = (ApacheHttpClient43Engine) client.httpEngine();
        HttpHost proxy = engine.getDefaultProxy();

        Assert.assertEquals("myoverrideproxy.com", proxy.getHostName());
        Assert.assertEquals(-1, proxy.getPort());
        Assert.assertEquals("http", proxy.getSchemeName());
        client.close();

        //modify and re-use builder...
        clientBuilder.defaultProxy(null);
        client = clientBuilder.build();
        engine = (ApacheHttpClient43Engine) client.httpEngine();
        proxy = engine.getDefaultProxy();

        Assert.assertEquals("myproxy.com", proxy.getHostName());
        Assert.assertEquals(8080, proxy.getPort());
        Assert.assertEquals("https", proxy.getSchemeName());
        client.close();

        //modify and re-use builder...
        clientBuilder.property(ResteasyClientBuilder.PROPERTY_PROXY_HOST, null)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_PORT, null)
                .property(ResteasyClientBuilder.PROPERTY_PROXY_SCHEME, null);
        client = clientBuilder.build();
        engine = (ApacheHttpClient43Engine) client.httpEngine();
        proxy = engine.getDefaultProxy();

        Assert.assertNull(proxy);
        client.close();
    }

    private static ClientBuilder createBuilder() {
        return ClientBuilder.newBuilder()
                .register(new ClientHttpEngineBuilder43());
    }
}
