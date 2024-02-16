package org.jboss.resteasy.test.client.other;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpAsyncClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4Resource;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4ResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test connection cleanup for ApacheHttpClient4Engine and URLConnectionEngine
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ApacheHttpClient43Test {

    protected static final Logger logger = Logger.getLogger(ApacheHttpClient43Test.class.getName());

    private Class<?> engine1 = ApacheHttpClient43Engine.class;
    private Class<?> engine2 = URLConnectionEngine.class;
    private Class<?> engine3 = ApacheHttpAsyncClient4Engine.class;

    private AtomicLong counter = new AtomicLong();

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ApacheHttpClient43Test.class.getSimpleName());
        war.addClass(ApacheHttpClient4Resource.class);
        return TestUtil.finishContainerPrepare(war, null, ApacheHttpClient4ResourceImpl.class);
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with correct request. System.gc is called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupGCBase() throws Exception {
        testConnectionCleanupGC(engine1);
        testConnectionCleanupGC(engine2);
        testConnectionCleanupGC(engine3);
    }

    protected void testConnectionCleanupGC(Class<?> engine) throws Exception {
        final Client client = createEngine(engine);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        runit(client, false);
                        System.gc();
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with correct request. System.gc is not called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupAuto() throws Exception {
        testConnectionCleanupAuto(engine1);
        testConnectionCleanupAuto(engine2);
        testConnectionCleanupAuto(engine3);
    }

    protected void testConnectionCleanupAuto(Class<?> engine) throws Exception {
        final Client client = createEngine(engine);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        runit(client, true);
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with correct request. System.gc is not called directly. Proxy is used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupProxy() throws Exception {
        testConnectionCleanupProxy(engine1);
        testConnectionCleanupProxy(engine2);
        testConnectionCleanupProxy(engine3);
    }

    protected void testConnectionCleanupProxy(Class<?> engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client
                .target(PortProviderUtil.generateBaseUrl(ApacheHttpClient43Test.class.getSimpleName()))
                .proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        String str = proxy.get();
                        Assertions.assertEquals("hello world", str, "Wrong response");
                        counter.incrementAndGet();
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with incorrect request. System.gc is called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupErrorGC() throws Exception {
        testConnectionCleanupErrorGC(engine1);
        testConnectionCleanupErrorGC(engine2);
        testConnectionCleanupErrorGC(engine3);
    }

    /**
     * This is regression test for RESTEASY-1273
     */
    protected void testConnectionCleanupErrorGC(Class<?> engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client
                .target(PortProviderUtil.generateBaseUrl(ApacheHttpClient43Test.class.getSimpleName()))
                .proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        callProxy(proxy);
                        System.gc();
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with incorrect request. System.gc is not called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupErrorNoGC() throws Exception {
        testConnectionCleanupErrorNoGC(engine1);
        testConnectionCleanupErrorNoGC(engine2);
        testConnectionCleanupErrorNoGC(engine3);
    }

    /**
     * This is regression test for RESTEASY-1273
     */
    protected void testConnectionCleanupErrorNoGC(Class<?> engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client
                .target(PortProviderUtil.generateBaseUrl(ApacheHttpClient43Test.class.getSimpleName()))
                .proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        try {
                            proxy.error();
                        } catch (NotFoundException e) {
                            Assertions.assertEquals(e.getResponse().getStatus(), 404);
                            e.getResponse().close();
                            counter.incrementAndGet();
                        }
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with incorrect request. System.gc is not called directly.
     *                Proxy is used. Data is sent during request.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionWithRequestBody() throws InterruptedException {
        testConnectionWithRequestBody(engine1);
        testConnectionWithRequestBody(engine2);
        testConnectionWithRequestBody(engine3);
    }

    protected void testConnectionWithRequestBody(Class<?> engine) throws InterruptedException {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client
                .target(PortProviderUtil.generateBaseUrl(ApacheHttpClient43Test.class.getSimpleName()))
                .proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        String res = proxy.getData(String.valueOf(j));
                        Assertions.assertNotNull(res, "Response should not be null");
                        counter.incrementAndGet();
                    }
                }
            };
        }

        for (int i = 0; i < 3; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 3; i++) {
            threads[i].join();
        }

        Assertions.assertEquals(30L, counter.get(), "Wrong count of requests");
    }

    private void callProxy(ApacheHttpClient4Resource proxy) {
        try {
            proxy.error();
        } catch (NotFoundException e) {
            Assertions.assertEquals(e.getResponse().getStatus(), 404);
            counter.incrementAndGet();
        }
    }

    private ResteasyClient createEngine(Class<?> engine) {
        RequestConfig reqConfig = RequestConfig.custom() // apache HttpClient specific
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(reqConfig)
                .setMaxConnTotal(3)
                .build();

        final ClientHttpEngine executor;

        if (engine.isAssignableFrom(ApacheHttpClient43Engine.class)) {
            executor = new ApacheHttpClient43Engine(httpClient);
        } else if (engine.isAssignableFrom(ApacheHttpAsyncClient4Engine.class)) {
            CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().setMaxConnTotal(3).build();
            executor = new ApacheHttpAsyncClient4Engine(client, true);
        } else if (engine.isAssignableFrom(URLConnectionEngine.class)) {
            executor = new URLConnectionEngine();
        } else {
            Assertions.fail("unknown engine");
            executor = null;
        }

        ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).httpEngine(executor).build();
        return client;
    }

    private void runit(Client client, boolean release) {
        WebTarget target = client
                .target(PortProviderUtil.generateBaseUrl(ApacheHttpClient43Test.class.getSimpleName() + "/test"));
        try {
            Response response = target.request().get();
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("hello world", response.readEntity(String.class),
                    "Wrong response");
            if (release) {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        counter.incrementAndGet();
    }
}
