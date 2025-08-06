package org.jboss.resteasy.test.client;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCache;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCacheFeature;
import org.jboss.resteasy.client.jaxrs.cache.LightweightBrowserCache;
import org.jboss.resteasy.test.client.resource.ClientCacheProxy;
import org.jboss.resteasy.test.client.resource.ClientCacheService;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for client cache
 */
@ExtendWith(ArquillianExtension.class)
public class ClientCacheTest {

    public static AtomicInteger count = new AtomicInteger(0);

    @BeforeEach
    public void setUp() throws Exception {
        count.set(0);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientCacheTest.class.getSimpleName());
        war.addClasses(ClientCacheProxy.class, ClientCacheTest.class, TestUtil.class, PortProviderUtil.class);
        war.addClasses(ClientCacheProxy.class, ClientCacheTest.class, TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, ClientCacheService.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ClientCacheTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Complex proxy test for client cache
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        count.set(0);
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateBaseUrl());
        target.register(BrowserCacheFeature.class);

        ClientCacheProxy proxy = target.proxy(ClientCacheProxy.class);
        String rtn;
        rtn = proxy.get();
        Assertions.assertEquals("hello world" + 1, rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        rtn = proxy.get();
        Assertions.assertEquals("hello world" + 1, rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        Thread.sleep(2000);
        rtn = proxy.get();
        Assertions.assertEquals("hello world" + 2, rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");
        rtn = proxy.get();
        Assertions.assertEquals("hello world" + 2, rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");

        // Test always good etag
        count.set(0);
        rtn = proxy.getAlwaysGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        rtn = proxy.getAlwaysGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        Thread.sleep(2000);
        rtn = proxy.getAlwaysGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");
        rtn = proxy.getAlwaysGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");

        // Test never good etag
        count.set(0);
        rtn = proxy.getNeverGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        rtn = proxy.getNeverGoodEtag();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        Thread.sleep(2000);
        rtn = proxy.getNeverGoodEtag();
        Assertions.assertEquals("hello2", rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");
        rtn = proxy.getNeverGoodEtag();
        Assertions.assertEquals("hello2", rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");

        // Test always validate etag
        count.set(0);
        rtn = proxy.getValidateEtagged();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");
        rtn = proxy.getValidateEtagged();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");
        rtn = proxy.getValidateEtagged();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(3, count.get(), "Wrong cache size");
        rtn = proxy.getValidateEtagged();
        Assertions.assertEquals("hello1", rtn, "Wrong response");
        Assertions.assertEquals(4, count.get(), "Wrong cache size");
        client.close();
    }

    /**
     * @tpTestDetails Test for max size of client cache
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMaxSize() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target(generateBaseUrl());
        target.register(BrowserCacheFeature.class);
        LightweightBrowserCache cache = (LightweightBrowserCache) target.getConfiguration()
                .getProperty(BrowserCache.class.getName());
        cache.setMaxBytes(20);
        ClientCacheProxy proxy = target.proxy(ClientCacheProxy.class);

        count.set(0);

        String rtn = proxy.getCacheit("1");
        Assertions.assertEquals("cachecache" + 1, rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");

        rtn = proxy.getCacheit("1");
        Assertions.assertEquals("cachecache" + 1, rtn, "Wrong response");
        Assertions.assertEquals(1, count.get(), "Wrong cache size");

        rtn = proxy.getCacheit("2");
        Assertions.assertEquals("cachecache" + 2, rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");

        rtn = proxy.getCacheit("2");
        Assertions.assertEquals("cachecache" + 2, rtn, "Wrong response");
        Assertions.assertEquals(2, count.get(), "Wrong cache size");

        rtn = proxy.getCacheit("1");
        Assertions.assertEquals("cachecache" + 3, rtn, "Wrong response");
        Assertions.assertEquals(3, count.get(), "Wrong cache size");
        client.close();
    }

    @Test
    public void testMaxSizeNoProxy() throws Exception {
        String url = PortProviderUtil.generateURL("/cache/cacheit/{id}", ClientCacheTest.class.getSimpleName());
        Client client = ClientBuilder.newClient();
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(url);
        LightweightBrowserCache cache = new LightweightBrowserCache();
        cache.setMaxBytes(20);
        BrowserCacheFeature cacheFeature = new BrowserCacheFeature();
        cacheFeature.setCache(cache);
        target.register(cacheFeature);

        count.set(0);

        String rtn = target.resolveTemplate("id", "1").request().get(String.class);
        Assertions.assertEquals("cachecache" + 1, rtn);
        Assertions.assertEquals(1, count.get());

        rtn = target.resolveTemplate("id", "1").request().get(String.class);
        Assertions.assertEquals("cachecache" + 1, rtn);
        Assertions.assertEquals(1, count.get());

        rtn = target.resolveTemplate("id", "2").request().get(String.class);
        Assertions.assertEquals("cachecache" + 2, rtn);
        Assertions.assertEquals(2, count.get());

        rtn = target.resolveTemplate("id", "2").request().get(String.class);
        Assertions.assertEquals("cachecache" + 2, rtn);
        Assertions.assertEquals(2, count.get());

        rtn = target.resolveTemplate("id", "1").request().get(String.class);
        Assertions.assertEquals("cachecache" + 3, rtn);
        Assertions.assertEquals(3, count.get());
        client.close();
    }
}
