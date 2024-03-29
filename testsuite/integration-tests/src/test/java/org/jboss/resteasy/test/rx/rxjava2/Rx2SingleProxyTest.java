package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava2.SingleRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2SingleResource;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2SingleResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.reactivex.Single;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 *
 *          In these tests, the server resource methods create and return objects of type Single<T>.
 *          The client uses a proxy that uses a SingleRxInvoker to get objects of type Single<T>.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class Rx2SingleProxyTest {

    private static ResteasyClient client;
    private static Rx2SingleResource proxy;
    private static CountDownLatch latch;
    private static AtomicReference<Object> value = new AtomicReference<Object>();

    private static List<Thing> xThingList = new ArrayList<Thing>();
    private static List<Thing> aThingList = new ArrayList<Thing>();

    static {
        for (int i = 0; i < 3; i++) {
            xThingList.add(new Thing("x"));
        }
        for (int i = 0; i < 3; i++) {
            aThingList.add(new Thing("a"));
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Rx2SingleProxyTest.class.getSimpleName());
        war.addClass(Thing.class);
        war.addClass(TRACE.class);
        war.addClass(RxScheduledExecutorService.class);
        war.addClass(TestException.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
        return TestUtil.finishContainerPrepare(war, null, Rx2SingleResourceImpl.class, TestExceptionMapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Rx2SingleProxyTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        proxy = client.target(generateURL("/")).proxy(Rx2SingleResource.class);
    }

    @BeforeEach
    public void before() throws Exception {
        latch = new CountDownLatch(1);
        value.set(null);
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    //////////////////////////////////////////////////////////////////////////////
    @Test
    public void testGet() throws Exception {
        Single<String> single = proxy.get();
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("x", value.get());
    }

    @Test
    public void testGetThing() throws Exception {
        Single<Thing> single = proxy.getThing();
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("x"), value.get());
    }

    @Test
    public void testGetThingList() throws Exception {
        Single<List<Thing>> single = proxy.getThingList();
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(xThingList, value.get());
    }

    @Test
    public void testPut() throws Exception {
        Single<String> single = proxy.put("a");
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("a", value.get());
    }

    @Test
    public void testPutThing() throws Exception {
        Single<Thing> single = proxy.putThing("a");
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("a"), value.get());
    }

    @Test
    public void testPutThingList() throws Exception {
        Single<List<Thing>> single = proxy.putThingList("a");
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(aThingList, value.get());
    }

    @Test
    public void testPost() throws Exception {
        Single<String> single = proxy.post("a");
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("a", value.get());
    }

    @Test
    public void testPostThing() throws Exception {
        Single<Thing> single = proxy.postThing("a");
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("a"), value.get());
    }

    @Test
    public void testPostThingList() throws Exception {
        Single<List<Thing>> single = proxy.postThingList("a");
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(aThingList, value.get());
    }

    @Test
    public void testDelete() throws Exception {
        Single<String> single = proxy.delete();
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("x", value.get());
    }

    @Test
    public void testDeleteThing() throws Exception {
        Single<Thing> single = proxy.deleteThing();
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("x"), value.get());
    }

    @Test
    public void testDeleteThingList() throws Exception {
        Single<List<Thing>> single = proxy.deleteThingList();
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(xThingList, value.get());
    }

    @Test
    public void testHead() throws Exception {
        Single<String> single = proxy.head();
        single.subscribe(
                (String s) -> {
                    value.set(s);
                    latch.countDown();
                },
                (Throwable t) -> throwableContains(t, "Input stream was empty"));
        Assertions.assertNull(value.get());
    }

    @Test
    public void testOptions() throws Exception {
        Single<String> single = proxy.options();
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("x", value.get());
    }

    @Test
    public void testOptionsThing() throws Exception {
        Single<Thing> single = proxy.optionsThing();
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("x"), value.get());
    }

    @Test
    public void testOptionsThingList() throws Exception {
        Single<List<Thing>> single = proxy.optionsThingList();
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(xThingList, value.get());
    }

    @Test
    public void testTrace() throws Exception {
        Single<String> single = proxy.trace();
        single.subscribe((String s) -> {
            value.set(s);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals("x", value.get());
    }

    @Test
    public void testTraceThing() throws Exception {
        Single<Thing> single = proxy.traceThing();
        single.subscribe((Thing t) -> {
            value.set(t);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(new Thing("x"), value.get());
    }

    @Test
    public void testTraceThingList() throws Exception {
        Single<List<Thing>> single = proxy.traceThingList();
        single.subscribe((List<Thing> l) -> {
            value.set(l);
            latch.countDown();
        });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(xThingList, value.get());
    }

    @Test
    public void testScheduledExecutorService() throws Exception {
        {
            RxScheduledExecutorService.used = false;
            Single<String> single = proxy.get();
            single.subscribe((String s) -> {
                value.set(s);
                latch.countDown();
            });
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertFalse(RxScheduledExecutorService.used);
            Assertions.assertEquals("x", value.get());
        }

        {
            latch = new CountDownLatch(1);
            RxScheduledExecutorService.used = false;
            RxScheduledExecutorService executor = new RxScheduledExecutorService();
            ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).executorService(executor).build();
            client.register(SingleRxInvokerProvider.class);
            Rx2SingleResource proxy = client.target(generateURL("/")).proxy(Rx2SingleResource.class);
            Single<String> single = proxy.get();
            single.subscribe((String s) -> {
                value.set(s);
                latch.countDown();
            });
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertTrue(RxScheduledExecutorService.used);
            Assertions.assertEquals("x", value.get());
            client.close();
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        Single<Thing> single = (Single<Thing>) proxy.exceptionUnhandled();
        single.subscribe(
                (Thing t) -> {
                },
                (Throwable t) -> {
                    value.set(t);
                    latch.countDown();
                });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Throwable t = unwrap((Throwable) value.get(), InternalServerErrorException.class);
        Assertions.assertNotNull(t);
        Assertions.assertTrue(t.getMessage().contains("500"));
    }

    @Test
    public void testHandledException() throws Exception {
        Single<Thing> single = (Single<Thing>) proxy.exceptionHandled();
        single.subscribe(
                (Thing t) -> {
                },
                (Throwable t) -> {
                    value.set(t);
                    latch.countDown();
                });
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Throwable t = unwrap((Throwable) value.get(), ClientErrorException.class);
        Assertions.assertNotNull(t);
        Assertions.assertTrue(t.getMessage().contains("444"));
    }

    @Test
    public void testGetTwoClients() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        ResteasyClient client1 = (ResteasyClient) ClientBuilder.newClient();
        client1.register(SingleRxInvokerProvider.class);
        Rx2SingleResource proxy1 = client1.target(generateURL("/")).proxy(Rx2SingleResource.class);
        Single<String> single1 = proxy1.get();

        ResteasyClient client2 = (ResteasyClient) ClientBuilder.newClient();
        client2.register(SingleRxInvokerProvider.class);
        Rx2SingleResource proxy2 = client2.target(generateURL("/")).proxy(Rx2SingleResource.class);
        Single<String> single2 = proxy2.get();

        single1.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });
        single2.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
        client1.close();
        client2.close();
    }

    @Test
    public void testGetTwoProxies() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        Rx2SingleResource proxy1 = client.target(generateURL("/")).proxy(Rx2SingleResource.class);
        Single<String> single1 = proxy1.get();

        Rx2SingleResource proxy2 = client.target(generateURL("/")).proxy(Rx2SingleResource.class);
        Single<String> single2 = proxy2.get();

        single1.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });
        single2.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
    }

    @Test
    public void testGetTwoSingles() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        Single<String> single1 = proxy.get();
        Single<String> single2 = proxy.get();

        single1.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });
        single2.subscribe((String s) -> {
            list.add(s);
            cdl.countDown();
        });

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(2, list.size());
        for (int i = 0; i < 2; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
    }

    private Throwable unwrap(Throwable t, Class<?> clazz) {
        while (t != null) {
            if (t.getClass().equals(clazz)) {
                return t;
            }
            t = t.getCause();
        }
        return null;
    }

    private static boolean throwableContains(Throwable t, String s) {
        while (t != null) {
            if (t.getMessage().contains(s)) {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}
