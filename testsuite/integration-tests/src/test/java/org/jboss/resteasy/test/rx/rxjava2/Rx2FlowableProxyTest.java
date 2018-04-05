package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;

import org.eclipse.yasson.JsonBindingProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
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
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 4.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class Rx2FlowableProxyTest {

    private static ResteasyClient client;
    private static Rx2FlowableResource proxy;
    private static CountDownLatch latch;

    private static List<String> stringList = new ArrayList<String>();
    private static List<Thing>  thingList = new ArrayList<Thing>();
    private static List<List<Thing>> thingListList = new ArrayList<List<Thing>>();
    private static ArrayList<byte[]> bytesList = new ArrayList<byte[]>();

    private final static List<String> xStringList = new ArrayList<String>();
    private final static List<String> aStringList = new ArrayList<String>();
    private final static List<Thing>  xThingList =  new ArrayList<Thing>();
    private final static List<Thing>  aThingList =  new ArrayList<Thing>();
    private final static List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
    private final static List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();

    static {
        for (int i = 0; i < 3; i++) {xStringList.add("x");}
        for (int i = 0; i < 3; i++) {aStringList.add("a");}
        for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
        for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
        for (int i = 0; i < 2; i++) {xThingListList.add(xThingList);}
        for (int i = 0; i < 2; i++) {aThingListList.add(aThingList);}
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(Rx2FlowableProxyTest.class.getSimpleName());
        war.addClass(Thing.class);
        war.addClass(TRACE.class);
        war.addClass(Bytes.class);
        war.addClass(RxScheduledExecutorService.class);
        war.addClass(TestException.class);
        war.addAsLibrary(TestUtil.resolveDependency("io.reactivex.rxjava2:rxjava:2.1.5"));
        war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava2:4.0.0-SNAPSHOT"));
        war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-json-binding-provider:4.0.0-SNAPSHOT"));
        war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
            + "Dependencies: org.reactivestreams\n"
            + "            : org.jboss.resteasy.resteasy-json-p-provider\n"
            ));
        war.addAsResource("META-INF/services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, Rx2FlowableResourceImpl.class, TestExceptionMapper.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, Rx2FlowableProxyTest.class.getSimpleName());
    }

    //////////////////////////////////////////////////////////////////////////////
    @BeforeClass
    public static void beforeClass() throws Exception {
        client = new ResteasyClientBuilder().build();
        client.register(FlowableRxInvokerProvider.class);
        client.register(JsonBindingProvider.class);
        proxy = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
    }

    @Before
    public void before() throws Exception {
        stringList.clear();
        thingList.clear();
        thingListList.clear();
        bytesList.clear();
        latch = new CountDownLatch(1);
    }
    
    //////////////////////////////////////////////////////////////////////////////
    @Test
    public void testGet() throws Exception {
        Flowable<String> flowable = proxy.get();
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xStringList, stringList);
    }

    @Test
    public void testGetThing() throws Exception {
        Flowable<Thing> flowable = proxy.getThing();
        flowable.subscribe(
            (Thing o) -> thingList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingList, thingList);
    }

    //   @Test
    public void testGetThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.getThingList();
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testGetBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.getBytes();
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPut() throws Exception {
        Flowable<String> flowable = proxy.put("a");
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aStringList, stringList);
    }

    @Test
    public void testPutThing() throws Exception {
        Flowable<Thing> flowable = proxy.putThing("a");
        flowable.subscribe(
            (Thing o) -> thingList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aThingList, thingList);
    }

    //@Test
    public void testPutThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.putThingList("a");
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aThingListList, thingListList);
    }
    
    @Test
    public void testPutBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.putBytes("3");
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testPost() throws Exception {
        Flowable<String> flowable = proxy.post("a");
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aStringList, stringList);
    }

    @Test
    public void testPostThing() throws Exception {
        Flowable<Thing> flowable = proxy.postThing("a");
        flowable.subscribe(
            (Thing o) -> thingList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aThingList, thingList);
    }

    //@Test
    public void testPostThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.postThingList("a");
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(aThingListList, thingListList);
    }

    @Test
    public void testPostBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.postBytes("3");
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }
    
    @Test
    public void testDelete() throws Exception {
        Flowable<String> flowable = proxy.delete();
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xStringList, stringList);
    }

    @Test
    public void testDeleteThing() throws Exception {
        Flowable<Thing> flowable = proxy.deleteThing();
        flowable.subscribe(
            (Thing o) -> thingList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingList, thingList);
    }

    //@Test
    public void testDeleteThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.deleteThingList();
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testDeleteBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.deleteBytes();
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }
    
    @Test
    @Ignore //TODO Fix
    public void testHead() throws Exception {
        Flowable<String> flowable = proxy.head();
        flowable.subscribe(
            (String s) -> System.out.println(s), // HEAD - no body
            (Throwable t) -> throwableContains(t, "Input stream was empty"));
    }

    @Test
    public void testOptions() throws Exception {
        Flowable<String> flowable = proxy.options();
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xStringList, stringList);
    }

    @Test
    public void testOptionsThing() throws Exception {
        Flowable<Thing> flowable = proxy.optionsThing();
        flowable.subscribe(
            (Thing o) -> thingList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingList, thingList);
    }

    //@Test
    public void testOptionsThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.optionsThingList();
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingListList, thingListList);
    }

    @Test
    public void testOptionsBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.optionsBytes();
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }
    
    @Test
    @Ignore // TRACE is disabled by default in Wildfly
    public void testTrace() throws Exception {
        Flowable<String> flowable = proxy.trace();
        flowable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xStringList, stringList);
    }

    @Test
    @Ignore // TRACE is disabled by default in Wildfly
    public void testTraceThing() throws Exception {
        Flowable<Thing> flowable = proxy.traceThing();
        flowable.subscribe(
            (Thing t) -> thingList.add(t),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingList, thingList);
    }

    @Test
    @Ignore // TRACE is disabled by default in Wildfly
    public void testTraceThingList() throws Exception {
        Flowable<List<Thing>> flowable = proxy.traceThingList();
        flowable.subscribe(
            (List<Thing> l) -> thingListList.add(l),
            (Throwable t) -> Assert.fail(t.getMessage()),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(xThingListList, thingListList);
    }
    
    @Test
    @Ignore // TRACE is disabled by default in Wildfly
    public void testTraceBytes() throws Exception {
        Flowable<byte[]> flowable = (Flowable<byte[]>) proxy.traceBytes();
        flowable.subscribe(
            (byte[] b) -> bytesList.add(b),
            (Throwable t) -> t.printStackTrace(),
            () -> latch.countDown());
        latch.await();
        Assert.assertEquals(3, bytesList.size());
        for (byte[] b : bytesList) {
            Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
        }
    }

    @Test
    public void testScheduledExecutorService () throws Exception {
        {
            RxScheduledExecutorService.used = false;
            Flowable<String> flowable = proxy.get();
            flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> Assert.fail(t.getMessage()),
                () -> latch.countDown());
            latch.await();
            Assert.assertFalse(RxScheduledExecutorService.used);
            Assert.assertEquals(xStringList, stringList);
        }

        {
            stringList.clear();
            latch = new CountDownLatch(1);
            RxScheduledExecutorService.used = false;
            RxScheduledExecutorService executor = new RxScheduledExecutorService();
            ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
            client.register(FlowableRxInvokerProvider.class);
            Rx2FlowableResource proxy = client.target(generateURL("/")).proxy(Rx2FlowableResource.class);
            Flowable<String> flowable = proxy.get();
            flowable.subscribe(
                (String o) -> stringList.add(o),
                (Throwable t) -> Assert.fail(t.getMessage()),
                () -> latch.countDown());
            latch.await();
            Assert.assertTrue(RxScheduledExecutorService.used);
            Assert.assertEquals(xStringList, stringList);
        }
    }

    @Test
    public void testUnhandledException() throws Exception {
        Flowable<Thing> flowable = proxy.exceptionUnhandled();
        AtomicReference<Object> value = new AtomicReference<Object>();
        flowable.subscribe(
            (Thing t) -> thingList.add(t),
            (Throwable t) -> {value.set(t); latch.countDown();},
            () -> latch.countDown());
        latch.await();
        Throwable t = (Throwable) value.get();
        Assert.assertEquals(InternalServerErrorException.class, t.getClass());
        Assert.assertTrue(t.getMessage().contains("500"));
    }

    @Test
    public void testHandledException() throws Exception {
        Flowable<Thing> flowable = proxy.exceptionHandled();
        AtomicReference<Object> value = new AtomicReference<Object>();
        flowable.subscribe(
            (Thing t) -> thingList.add(t),
            (Throwable t) -> {value.set(t); latch.countDown();},
            () -> latch.countDown());
        latch.await();
        Throwable t = (Throwable) value.get();
        Assert.assertEquals(ClientErrorException.class, t.getClass());
        Assert.assertTrue(t.getMessage().contains("444"));
    }

    @Test
    public void testGetTwoClients() throws Exception {
        CountDownLatch cdl = new CountDownLatch(2);
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

        ResteasyClient client1 = new ResteasyClientBuilder().build();
        client1.register(FlowableRxInvokerProvider.class);
        Rx2FlowableResource proxy1 = client1.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable1 = (Flowable<String>) proxy1.get();

        ResteasyClient client2 = new ResteasyClientBuilder().build();
        client2.register(FlowableRxInvokerProvider.class);
        Rx2FlowableResource proxy2 = client2.target(generateURL("/")).proxy(Rx2FlowableResource.class);
        Flowable<String> flowable2 = (Flowable<String>) proxy2.get();

        flowable1.subscribe(
            (String o) -> list.add(o),
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        flowable2.subscribe(
            (String o) -> list.add(o),
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        cdl.await();
        Assert.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("x", list.get(i));
        }
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
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        flowable2.subscribe(
            (String o) -> list.add(o),
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        cdl.await();
        Assert.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("x", list.get(i));
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
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        flowable2.subscribe(
            (String o) -> list.add(o),
            (Throwable t) -> t.printStackTrace(),
            () -> cdl.countDown());

        cdl.await();
        Assert.assertEquals(6, list.size());
        for (int i = 0; i < 6; i++) {
            Assert.assertEquals("x", list.get(i));
        }
    }

    private static boolean throwableContains(Throwable t, String s) {
        while (t != null) {
            if (t.getMessage().contains(s))
            {
                return true;
            }
            t = t.getCause();
        }
        return false;
    }
}