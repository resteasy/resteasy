package org.jboss.resteasy.test.client;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCache;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCacheFeature;
import org.jboss.resteasy.client.jaxrs.cache.LightweightBrowserCache;
import org.jboss.resteasy.test.client.resource.ClientCacheProxy;
import org.jboss.resteasy.test.client.resource.ClientCacheService;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.PropertyPermission;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LoggingPermission;


/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for client cache
 */
@RunWith(Arquillian.class)
public class ClientCacheTest {

    public static AtomicInteger count = new AtomicInteger(0);

    @Before
    public void setUp() throws Exception {
        count.set(0);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientCacheTest.class.getSimpleName());
        war.addClasses(ClientCacheProxy.class, ClientCacheTest.class, TestUtil.class, PortProviderUtil.class);
        // Arquillian in the deployment and use of PortProviderUtil and Test util in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(new ReflectPermission("suppressAccessChecks"),
                new LoggingPermission("control", ""),
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")
        ), "permissions.xml");
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
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateBaseUrl());
        target.register(BrowserCacheFeature.class);

        ClientCacheProxy proxy = target.proxy(ClientCacheProxy.class);
        String rtn;
        rtn = proxy.get();
        Assert.assertEquals("Wrong response", "hello world" + 1, rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        rtn = proxy.get();
        Assert.assertEquals("Wrong response", "hello world" + 1, rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        Thread.sleep(2000);
        rtn = proxy.get();
        Assert.assertEquals("Wrong response", "hello world" + 2, rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());
        rtn = proxy.get();
        Assert.assertEquals("Wrong response", "hello world" + 2, rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());

        // Test always good etag
        count.set(0);
        rtn = proxy.getAlwaysGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        rtn = proxy.getAlwaysGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        Thread.sleep(2000);
        rtn = proxy.getAlwaysGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());
        rtn = proxy.getAlwaysGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());

        // Test never good etag
        count.set(0);
        rtn = proxy.getNeverGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        rtn = proxy.getNeverGoodEtag();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        Thread.sleep(2000);
        rtn = proxy.getNeverGoodEtag();
        Assert.assertEquals("Wrong response", "hello2", rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());
        rtn = proxy.getNeverGoodEtag();
        Assert.assertEquals("Wrong response", "hello2", rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());

        // Test always validate etag
        count.set(0);
        rtn = proxy.getValidateEtagged();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());
        rtn = proxy.getValidateEtagged();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());
        rtn = proxy.getValidateEtagged();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 3, count.get());
        rtn = proxy.getValidateEtagged();
        Assert.assertEquals("Wrong response", "hello1", rtn);
        Assert.assertEquals("Wrong cache size", 4, count.get());
        client.close();
    }

    /**
     * @tpTestDetails Test for max size of client cache
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMaxSize() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(generateBaseUrl());
        target.register(BrowserCacheFeature.class);
        LightweightBrowserCache cache = (LightweightBrowserCache) target.getConfiguration().getProperty(BrowserCache.class.getName());
        cache.setMaxBytes(20);
        ClientCacheProxy proxy = target.proxy(ClientCacheProxy.class);

        count.set(0);

        String rtn = proxy.getCacheit("1");
        Assert.assertEquals("Wrong response", "cachecache" + 1, rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());

        rtn = proxy.getCacheit("1");
        Assert.assertEquals("Wrong response", "cachecache" + 1, rtn);
        Assert.assertEquals("Wrong cache size", 1, count.get());

        rtn = proxy.getCacheit("2");
        Assert.assertEquals("Wrong response", "cachecache" + 2, rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());

        rtn = proxy.getCacheit("2");
        Assert.assertEquals("Wrong response", "cachecache" + 2, rtn);
        Assert.assertEquals("Wrong cache size", 2, count.get());

        rtn = proxy.getCacheit("1");
        Assert.assertEquals("Wrong response", "cachecache" + 3, rtn);
        Assert.assertEquals("Wrong cache size", 3, count.get());
        client.close();
    }

   @Test
   public void testMaxSizeNoProxy() throws Exception {
      String url = PortProviderUtil.generateURL("/cache/cacheit/{id}", ClientCacheTest.class.getSimpleName());
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(url);
      LightweightBrowserCache cache = new LightweightBrowserCache();
      cache.setMaxBytes(20);
      BrowserCacheFeature cacheFeature = new BrowserCacheFeature();
      cacheFeature.setCache(cache);
      target.register(cacheFeature);

      count.set(0);

      String rtn = target.resolveTemplate("id", "1").request().get(String.class);
      Assert.assertEquals("cachecache" + 1, rtn);
      Assert.assertEquals(1, count.get());

      rtn = target.resolveTemplate("id", "1").request().get(String.class);
      Assert.assertEquals("cachecache" + 1, rtn);
      Assert.assertEquals(1, count.get());

      rtn = target.resolveTemplate("id", "2").request().get(String.class);
      Assert.assertEquals("cachecache" + 2, rtn);
      Assert.assertEquals(2, count.get());

      rtn = target.resolveTemplate("id", "2").request().get(String.class);
      Assert.assertEquals("cachecache" + 2, rtn);
      Assert.assertEquals(2, count.get());

      rtn = target.resolveTemplate("id", "1").request().get(String.class);
      Assert.assertEquals("cachecache" + 3, rtn);
      Assert.assertEquals(3, count.get());
   }
}
