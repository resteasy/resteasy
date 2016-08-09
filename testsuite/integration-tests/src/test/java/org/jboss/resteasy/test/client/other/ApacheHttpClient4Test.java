package org.jboss.resteasy.test.client.other;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.client.jaxrs.engines.URLConnectionEngine;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4Resource;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4ResourceImpl;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test connection cleanup for ApacheHttpClient4Engine and URLConnectionEngine
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ApacheHttpClient4Test {

    protected static final Logger logger = LogManager.getLogger(ApacheHttpClient4Test.class.getName());

    private Class engine1 = ApacheHttpClient4Engine.class;
    private Class engine2 = URLConnectionEngine.class;

    private AtomicLong counter = new AtomicLong();

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ApacheHttpClient4Test.class.getSimpleName());
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
    }

    public void testConnectionCleanupGC(Class engine) throws Exception {
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with correct request. System.gc is not called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupAuto() throws Exception {
        testConnectionCleanupAuto(engine1);
        testConnectionCleanupAuto(engine2);
    }

    public void testConnectionCleanupAuto(Class engine) throws Exception {
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with correct request. System.gc is not called directly. Proxy is used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupProxy() throws Exception {
        testConnectionCleanupProxy(engine1);
        testConnectionCleanupProxy(engine2);
    }

    public void testConnectionCleanupProxy(Class engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client.target(PortProviderUtil.generateBaseUrl(ApacheHttpClient4Test.class.getSimpleName())).proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];


        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        String str = proxy.get();
                        Assert.assertEquals("Wrong response", "hello world", str);
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with incorrect request. System.gc is called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupErrorGC() throws Exception {
        testConnectionCleanupErrorGC(engine1);
        testConnectionCleanupErrorGC(engine2);
    }

    /**
     * This is regression test for RESTEASY-1273
     */
    public void testConnectionCleanupErrorGC(Class engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client.target(PortProviderUtil.generateBaseUrl(ApacheHttpClient4Test.class.getSimpleName())).proxy(ApacheHttpClient4Resource.class);
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
    }

    /**
     * @tpTestDetails Create 3 threads and test GC with incorrect request. System.gc is not called directly. Proxy is not used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConnectionCleanupErrorNoGC() throws Exception {
        testConnectionCleanupErrorNoGC(engine1);
        testConnectionCleanupErrorNoGC(engine2);
    }

    /**
     * This is regression test for RESTEASY-1273
     */
    public void testConnectionCleanupErrorNoGC(Class engine) throws Exception {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client.target(PortProviderUtil.generateBaseUrl(ApacheHttpClient4Test.class.getSimpleName())).proxy(ApacheHttpClient4Resource.class);
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
                            Assert.assertEquals(e.getResponse().getStatus(), 404);
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
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
    }

    public void testConnectionWithRequestBody(Class engine) throws InterruptedException {
        final ResteasyClient client = createEngine(engine);
        final ApacheHttpClient4Resource proxy = client.target(PortProviderUtil.generateBaseUrl(ApacheHttpClient4Test.class.getSimpleName())).proxy(ApacheHttpClient4Resource.class);
        counter.set(0);

        Thread[] threads = new Thread[3];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10; j++) {
                        String res = proxy.getData(String.valueOf(j));
                        Assert.assertNotNull("Response should not be null", res);
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

        Assert.assertEquals("Wrong count of requests", 30L, counter.get());
    }

    private void callProxy(ApacheHttpClient4Resource proxy) {
        try {
            proxy.error();
        } catch (NotFoundException e) {
            Assert.assertEquals(e.getResponse().getStatus(), 404);
            counter.incrementAndGet();
        }
    }

    @SuppressWarnings(value = "unchecked")
    private ResteasyClient createEngine(Class engine) {
        RequestConfig reqConfig = RequestConfig.custom()   // apache HttpClient specific
                .setConnectTimeout(5000)
                .setSocketTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(reqConfig)
                .setMaxConnTotal(3)
                .build();

        final ClientHttpEngine executor;

        if (engine.isAssignableFrom(ApacheHttpClient4Engine.class)) {
            executor = new ApacheHttpClient4Engine(httpClient);
        } else {
            executor = new URLConnectionEngine();
        }


        ResteasyClient client = new ResteasyClientBuilder().httpEngine(executor).build();
        return client;
    }

    private void runit(Client client, boolean release) {
        WebTarget target = client.target(PortProviderUtil.generateBaseUrl(ApacheHttpClient4Test.class.getSimpleName() + "/test"));
        try {
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assert.assertEquals("Wrong response", "hello world", response.readEntity(String.class));
            if (release) {
                response.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        counter.incrementAndGet();
    }
}
