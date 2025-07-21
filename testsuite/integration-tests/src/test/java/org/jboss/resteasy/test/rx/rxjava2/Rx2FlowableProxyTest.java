package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava2.FlowableRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2FlowableResource;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2FlowableResourceImpl;
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

import io.reactivex.Flowable;

/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 *
 *          In these tests, the server uses Flowables to create results asynchronously and streams the elements
 *          of the Flowables as they are created.
 *
 *          The client uses a proxy that calls an FlowableRxInvoker.
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class Rx2FlowableProxyTest {

    private static ResteasyClient client;
    private static Rx2FlowableResource proxy;
    private static CountDownLatch latch;
    private static AtomicInteger errors;

    private static List<String> stringList = new ArrayList<String>();
    private static List<Thing> thingList = new ArrayList<Thing>();
    private static List<List<Thing>> thingListList = new ArrayList<List<Thing>>();
    private static ArrayList<byte[]> bytesList = new ArrayList<byte[]>();

    private static AtomicReference<Object> value = new AtomicReference<Object>();
    private static final List<String> xStringList = new ArrayList<String>();
    private static final List<String> aStringList = new ArrayList<String>();
    private static final List<Thing> xThingList = new ArrayList<Thing>();
    private static final List<Thing> aThingList = new ArrayList<Thing>();
    private static final List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
    private static final List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();

    static {
        for (int i = 0; i < 3; i++) {
            xStringList.add("x");
        }
        for (int i = 0; i < 3; i++) {
            aStringList.add("a");
        }
        for (int i = 0; i < 3; i++) {
            xThingList.add(new Thing("x"));
        }
        for (int i = 0; i < 3; i++) {
            aThingList.add(new Thing("a"));
        }
        for (int i = 0; i < 2; i++) {
            xThingListList.add(xThingList);
        }
        for (int i = 0; i < 2; i++) {
            aThingListList.add(aThingList);
        }
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Rx2FlowableProxyTest.class.getSimpleName());
        war.addClass(Thing.class);
        war.addClass(TRACE.class);
        war.addClass(Bytes.class);
        war.addClass(RxScheduledExecutorService.class);
        war.addClass(TestException.class);
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
                + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
        return TestUtil.finishContainerPrepare(war, null, Rx2FlowableResourceImpl.class, TestExceptionMapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Rx2FlowableProxyTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeAll
    public static void beforeClass() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        proxy = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    @BeforeEach
    public void before() throws Exception {
        stringList.clear();
        thingList.clear();
        thingListList.clear();
        bytesList.clear();
        latch = new CountDownLatch(1);
        errors = new AtomicInteger(0);
        value.set(null);
    }

    //////////////////////////////////////////////////////////////////////////////
    @Test
    public void testGet() throws Exception {
        Flowable<String> flowable = proxy.get();
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xStringList, stringList);
    }

    @Test
    public void testGetThing() throws Exception {
        Flowable<Thing> flowable = proxy.getThing();
        flowable.subscribe(
                (Thing o) -> thingList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingList, thingList);
    }

    @Test
    public void testGetThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.getThingList();
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testGetBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.getBytes();
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPut() throws Exception {
        Flowable<String> flowable = proxy.put("a");
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aStringList, stringList);
    }

    @Test
    public void testPutThing() throws Exception {
        Flowable<Thing> flowable = proxy.putThing("a");
        flowable.subscribe(
                (Thing o) -> thingList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aThingList, thingList);
    }

    @Test
    public void testPutThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.putThingList("a");
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aThingListList, thingListList);
    }

    @Test
    public void testPutBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.putBytes("3");
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPost() throws Exception {
        Flowable<String> flowable = proxy.post("a");
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aStringList, stringList);
    }

    @Test
    public void testPostThing() throws Exception {
        Flowable<Thing> flowable = proxy.postThing("a");
        flowable.subscribe(
                (Thing o) -> thingList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aThingList, thingList);
    }

    @Test
    public void testPostThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.postThingList("a");
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(aThingListList, thingListList);
    }

    @Test
    public void testPostBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.postBytes("3");
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testDelete() throws Exception {
        Flowable<String> flowable = proxy.delete();
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xStringList, stringList);
    }

    @Test
    public void testDeleteThing() throws Exception {
        Flowable<Thing> flowable = proxy.deleteThing();
        flowable.subscribe(
                (Thing o) -> thingList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingList, thingList);
    }

    @Test
    public void testDeleteThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.deleteThingList();
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testDeleteBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.deleteBytes();
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testHead() throws Exception {
        Flowable<String> flowable = proxy.head();

        flowable.subscribe(
                (String s) -> value.set(s), // HEAD - no body
                (Throwable t) -> throwableContains(t, "Input stream was empty"));

        Assertions.assertNull(value.get());
    }

    @Test
    public void testOptions() throws Exception {
        Flowable<String> flowable = proxy.options();
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xStringList, stringList);
    }

    @Test
    public void testOptionsThing() throws Exception {
        Flowable<Thing> flowable = proxy.optionsThing();
        flowable.subscribe(
                (Thing o) -> thingList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingList, thingList);
    }

    @Test
    public void testOptionsThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.optionsThingList();
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testOptionsBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.optionsBytes();
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testTrace() throws Exception {
        Flowable<String> flowable = proxy.trace();
        flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xStringList, stringList);
    }

    @Test
    public void testTraceThing() throws Exception {
        Flowable<Thing> flowable = proxy.traceThing();
        flowable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingList, thingList);
    }

    @Test
    public void testTraceThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.traceThingList();
        flowable.subscribe(
                (List<Thing> l) -> thingListList.add(l),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testTraceBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.traceBytes();
        flowable.subscribe(
                (byte[] b) -> bytesList.add(b),
                (Throwable t) -> errors.incrementAndGet(),
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assertions.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testScheduledExecutorService() throws Exception {
        {
            RxScheduledExecutorService.used = false;
            Flowable<String> flowable = proxy.get();
            flowable.subscribe(
                    (String o) -> stringList.add(o),
                    (Throwable t) -> errors.incrementAndGet(),
                    () -> latch.countDown());
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertEquals(0, errors.get());
            Assertions.assertFalse(RxScheduledExecutorService.used);
            Assertions.assertEquals(xStringList, stringList);
        }

        {
            stringList.clear();
            latch = new CountDownLatch(1);
            RxScheduledExecutorService.used = false;
            RxScheduledExecutorService executor = new RxScheduledExecutorService();
            ResteasyClient client = ((ResteasyClientBuilder) ClientBuilder.newBuilder()).executorService(executor).build();
            client.register(FlowableRxInvokerProvider.class);
            Rx2FlowableResource proxy = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
            Flowable<String> flowable = proxy.get();
            flowable.subscribe(
                    (String o) -> stringList.add(o),
                    (Throwable t) -> errors.incrementAndGet(),
                    () -> latch.countDown());
            boolean waitResult = latch.await(30, TimeUnit.SECONDS);
            Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
            Assertions.assertEquals(0, errors.get());
            Assertions.assertTrue(RxScheduledExecutorService.used);
            Assertions.assertEquals(xStringList, stringList);
            client.close();
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        Flowable<Thing> flowable = proxy.exceptionUnhandled();
        AtomicReference<Object> value = new AtomicReference<Object>();
        flowable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> {
                    value.set(t);
                    latch.countDown();
                },
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Throwable t = (Throwable) value.get();
        Assertions.assertEquals(InternalServerErrorException.class, t.getClass());
        Assertions.assertTrue(t.getMessage().contains("500"));
    }

    @Test
    public void testHandledException() throws Exception {
        Flowable<Thing> flowable = proxy.exceptionHandled();
        AtomicReference<Object> value = new AtomicReference<Object>();
        flowable.subscribe(
                (Thing t) -> thingList.add(t),
                (Throwable t) -> {
                    value.set(t);
                    latch.countDown();
                },
                () -> latch.countDown());
        boolean waitResult = latch.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Throwable t = (Throwable) value.get();
        Assertions.assertEquals(ClientErrorException.class, t.getClass());
        Assertions.assertTrue(t.getMessage().contains("444"));
    }

    @Test
    public void testGetTwoClients() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        ResteasyClient client1 = (ResteasyClient) ClientBuilder.newClient();
        client1.register(FlowableRxInvokerProvider.class);
        Rx2FlowableResource proxy1 = client1.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable1 = (Flowable<String>) proxy1.get();

        ResteasyClient client2 = (ResteasyClient) ClientBuilder.newClient();
        client2.register(FlowableRxInvokerProvider.class);
        Rx2FlowableResource proxy2 = client2.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable2 = (Flowable<String>) proxy2.get();

        flowable1.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        flowable2.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
        client1.close();
        client2.close();
    }

    @Test
    public void testGetTwoProxies() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        Rx2FlowableResource proxy1 = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable1 = (Flowable<String>) proxy1.get();

        Rx2FlowableResource proxy2 = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable2 = (Flowable<String>) proxy2.get();

        flowable1.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        flowable2.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
    }

    @Test
    public void testGetTwoFlowables() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        Flowable<String> flowable1 = (Flowable<String>) proxy.get();
        Flowable<String> flowable2 = (Flowable<String>) proxy.get();

        flowable1.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        flowable2.subscribe(
                (String o) -> list.add(o),
                (Throwable t) -> errors.incrementAndGet(),
                () -> cdl.countDown());

        boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
        Assertions.assertTrue(waitResult, "Waiting for event to be delivered has timed out.");
        Assertions.assertEquals(0, errors.get());
        Assertions.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assertions.assertEquals("x", list.get(i));
        }
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
