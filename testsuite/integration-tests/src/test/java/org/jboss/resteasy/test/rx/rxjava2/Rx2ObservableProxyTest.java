package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava2.ObservableRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.Bytes;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ObservableResource;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2ObservableResourceImpl;
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
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import io.reactivex.Observable;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * In these tests, the server uses Observables to create results asynchronously and streams the elements
 * of the Observables as they are created.
 *
 * The client uses a proxy that calls an ObservableRxInvoker.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class Rx2ObservableProxyTest {

   private static ResteasyClient client;
   private static Rx2ObservableResource proxy;
   private static CountDownLatch latch;
   private static AtomicInteger errors;

   private static AtomicReference<Object> value = new AtomicReference<Object>();
   private static List<String> stringList = new ArrayList<String>();
   private static List<Thing>  thingList = new ArrayList<Thing>();
   private static List<List<Thing>> thingListList = new ArrayList<List<Thing>>();
   private static List<byte[]> bytesList = new ArrayList<byte[]>();

   private static final List<String> xStringList = new ArrayList<String>();
   private static final List<String> aStringList = new ArrayList<String>();
   private static final List<Thing>  xThingList =  new ArrayList<Thing>();
   private static final List<Thing>  aThingList =  new ArrayList<Thing>();
   private static final List<List<Thing>> xThingListList = new ArrayList<List<Thing>>();
   private static final List<List<Thing>> aThingListList = new ArrayList<List<Thing>>();

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
      WebArchive war = TestUtil.prepareArchive(Rx2ObservableProxyTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(TRACE.class);
      war.addClass(Bytes.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, Rx2ObservableResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Rx2ObservableProxyTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      proxy = client.target(generateURL("/")).proxy(Rx2ObservableResource.class);
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
      errors = new AtomicInteger(0);
      value.set(null);
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      Observable<String> observable = proxy.get();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xStringList, stringList);
   }

   @Test
   public void testGetThing() throws Exception {
      Observable<Thing> observable = proxy.getThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingList, thingList);
   }

   @Test
   public void testGetThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.getThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingListList, thingListList);
   }

   @Test
   public void testGetBytes() throws Exception {
      Observable<byte[]> observable = proxy.getBytes();
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testPut() throws Exception {
      Observable<String> observable = proxy.put("a");
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aStringList, stringList);
   }

   @Test
   public void testPutThing() throws Exception {
      Observable<Thing> observable = proxy.putThing("a");
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aThingList, thingList);
   }

   @Test
   public void testPutThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.putThingList("a");
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aThingListList, thingListList);
   }

   @Test
   public void testPutBytes() throws Exception {
      Observable<byte[]> observable = proxy.putBytes("3");
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testPost() throws Exception {
      Observable<String> observable = proxy.post("a");
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aStringList, stringList);
   }

   @Test
   public void testPostThing() throws Exception {
      Observable<Thing> observable = proxy.postThing("a");
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aThingList, thingList);
   }

   @Test
   public void testPostThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.postThingList("a");
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(aThingListList, thingListList);
   }

   @Test
   public void testPostBytes() throws Exception {
      Observable<byte[]> observable = proxy.postBytes("3");
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testDelete() throws Exception {
      Observable<String> observable = proxy.delete();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xStringList, stringList);
   }

   @Test
   public void testDeleteThing() throws Exception {
      Observable<Thing> observable = proxy.deleteThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingList, thingList);
   }

   @Test
   public void testDeleteThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.deleteThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingListList, thingListList);
   }

   @Test
   public void testDeleteBytes() throws Exception {
      Observable<byte[]> observable = proxy.deleteBytes();
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testHead() throws Exception {
      Observable<String> observable = proxy.head();
      observable.subscribe(
         (String s) -> value.set(s), // HEAD - no body
         (Throwable t) -> throwableContains(t, "Input stream was empty"));

      Assert.assertNull(value.get());
   }

   @Test
   public void testOptions() throws Exception {
      Observable<String> observable = proxy.options();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xStringList, stringList);
   }

   @Test
   public void testOptionsThing() throws Exception {
      Observable<Thing> observable = proxy.optionsThing();
      observable.subscribe(
         (Thing o) -> thingList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingList, thingList);
   }

   @Test
   public void testOptionsThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.optionsThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingListList, thingListList);
   }

   @Test
   public void testOptionsBytes() throws Exception {
      Observable<byte[]> observable = proxy.optionsBytes();
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      Observable<String> observable = proxy.trace();
      observable.subscribe(
         (String o) -> stringList.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xStringList, stringList);
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      Observable<Thing> observable = proxy.traceThing();
      observable.subscribe(
         (Thing t) -> thingList.add(t),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingList, thingList);
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      Observable<List<Thing>> observable = proxy.traceThingList();
      observable.subscribe(
         (List<Thing> l) -> thingListList.add(l),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(xThingListList, thingListList);
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceBytes() throws Exception {
      Observable<byte[]> observable = proxy.traceBytes();
      observable.subscribe(
         (byte[] b) -> bytesList.add(b),
         (Throwable t) -> errors.incrementAndGet(),
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(3, bytesList.size());
      for (byte[] b : bytesList) {
         Assert.assertTrue(Arrays.equals(Bytes.BYTES, b));
      }
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         Observable<String> observable = proxy.get();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> errors.incrementAndGet(),
            () -> latch.countDown());
         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertEquals(0, errors.get());
         Assert.assertFalse(RxScheduledExecutorService.used);
         Assert.assertEquals(xStringList, stringList);
      }

      {
         stringList.clear();
         latch = new CountDownLatch(1);
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(ObservableRxInvokerProvider.class);
         Rx2ObservableResource proxy = client.target(generateURL("/")).proxy(Rx2ObservableResource.class);
         Observable<String> observable = proxy.get();
         observable.subscribe(
            (String o) -> stringList.add(o),
            (Throwable t) -> errors.incrementAndGet(),
            () -> latch.countDown());
         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertEquals(0, errors.get());
         Assert.assertTrue(RxScheduledExecutorService.used);
         Assert.assertEquals(xStringList, stringList);
      }
   }

   @Test
   public void testUnhandledException() throws Exception {
      Observable<Thing> observable = proxy.exceptionUnhandled();
      AtomicReference<Object> value = new AtomicReference<Object>();
      observable.subscribe(
         (Thing t) -> thingList.add(t),
         (Throwable t) -> {value.set(t); latch.countDown();},
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Throwable t = (Throwable) value.get();
      Assert.assertEquals(InternalServerErrorException.class, t.getClass());
      Assert.assertTrue(t.getMessage().contains("500"));
   }

   @Test
   public void testHandledException() throws Exception {
      Observable<Thing> observable = proxy.exceptionHandled();
      AtomicReference<Object> value = new AtomicReference<Object>();
      observable.subscribe(
         (Thing t) -> thingList.add(t),
         (Throwable t) -> {value.set(t); latch.countDown();},
         () -> latch.countDown());
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Throwable t = (Throwable) value.get();
      Assert.assertEquals(ClientErrorException.class, t.getClass());
      Assert.assertTrue(t.getMessage().contains("444"));
   }

   @Test
   public void testGetTwoClients() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      ResteasyClient client1 = new ResteasyClientBuilder().build();
      client1.register(ObservableRxInvokerProvider.class);
      Rx2ObservableResource proxy1 = client1.target(generateURL("/")).proxy(Rx2ObservableResource.class);
      Observable<String> observable1 = proxy1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(ObservableRxInvokerProvider.class);
      Rx2ObservableResource proxy2 = client2.target(generateURL("/")).proxy(Rx2ObservableResource.class);
      Observable<String> observable2 = proxy2.get();

      observable1.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      observable2.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(6, list.size());
      for (int i = 0; i < 6; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoProxies() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      Rx2ObservableResource proxy1 = client.target(generateURL("/")).proxy(Rx2ObservableResource.class);
      Rx2ObservableResource proxy2 = client.target(generateURL("/")).proxy(Rx2ObservableResource.class);

      Observable<String> observable1 = proxy1.get();
      Observable<String> observable2 = proxy2.get();

      observable1.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      observable2.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
      Assert.assertEquals(6, list.size());
      for (int i = 0; i < 6; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoObservables() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      Observable<String> observable1 = proxy.get();
      Observable<String> observable2 = proxy.get();

      observable1.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      observable2.subscribe(
         (String o) -> list.add(o),
         (Throwable t) -> errors.incrementAndGet(),
         () -> cdl.countDown());

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(0, errors.get());
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
