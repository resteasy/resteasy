package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava2.SingleRxInvoker;
import org.jboss.resteasy.rxjava2.SingleRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TRACE;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.test.rx.rxjava2.resource.Rx2SingleResourceImpl;
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

import io.reactivex.Single;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * In these tests, the server resource methods create and return objects of type Single<T>.
 * The client uses a SingleRxInvoker to get objects of type Single<T>.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class Rx2SingleTest {

   private static ResteasyClient client;
   private static CountDownLatch latch;
   private static AtomicReference<Object> value = new AtomicReference<Object>();
   private static List<Thing>  xThingList =  new ArrayList<Thing>();
   private static List<Thing>  aThingList =  new ArrayList<Thing>();
   private static Entity<String> aEntity = Entity.entity("a", MediaType.TEXT_PLAIN_TYPE);
   private static GenericType<List<Thing>> LIST_OF_THING = new GenericType<List<Thing>>() {};

   static {
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(Rx2SingleTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(TRACE.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, Rx2SingleResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Rx2SingleTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
   }

   @Before
   public void before() throws Exception {
      latch = new CountDownLatch(1);
      value.set(null);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.get();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   public void testGetString() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<String> single = invoker.get(String.class);
      single.subscribe((String s) -> {value.set(s); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   public void testGetThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.get(Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("x"), value.get());
   }

   @Test
   public void testGetThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.get(LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(xThingList, value.get());
   }

   @Test
   public void testPut() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.put(aEntity);
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("a", value.get());
   }

   @Test
   public void testPutThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.put(aEntity, Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("a"), value.get());
   }

   @Test
   public void testPutThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/put/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.put(aEntity, LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(aThingList, value.get());
   }

   @Test
   public void testPost() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.post(aEntity);
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("a", value.get());
   }

   @Test
   public void testPostThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.post(aEntity, Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("a"), value.get());
   }

   @Test
   public void testPostThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.post(aEntity, LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(aThingList, value.get());
   }

   @Test
   public void testDelete() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.delete();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   public void testDeleteThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.delete(Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("x"), value.get());
   }

   @Test
   public void testDeleteThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/delete/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.delete(LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(xThingList, value.get());
   }

   @Test
   public void testHead() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/head/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.head();
      single.subscribe(
            (Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();},
            (Throwable t) -> throwableContains(t, "Input stream was empty"));
      Assert.assertNull(value.get());
   }

   @Test
   public void testOptions() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.options();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   public void testOptionsThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.options(Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("x"), value.get());
   }

   @Test
   public void testOptionsThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/options/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.options(LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(xThingList, value.get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.trace();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.trace(Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("x"), value.get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/trace/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.trace(LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(xThingList, value.get());
   }

   @Test
   public void testMethodGet() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.method("GET");
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("x", value.get());
   }

   @Test
   public void testMethodGetThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.method("GET", Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("x"), value.get());
   }

   @Test
   public void testMethodGetThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.method("GET", LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(xThingList, value.get());
   }

   @Test
   public void testMethodPost() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single = invoker.method("POST", aEntity);
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals("a", value.get());
   }

   @Test
   public void testMethodPostThing() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = invoker.method("POST", aEntity, Thing.class);
      single.subscribe((Thing t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(new Thing("a"), value.get());
   }

   @Test
   public void testMethodPostThingList() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(SingleRxInvoker.class);
      Single<List<Thing>> single = invoker.method("POST", aEntity, LIST_OF_THING);
      single.subscribe((List<Thing> l) -> {value.set(l); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(aThingList, value.get());
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
         Single<Response> single = invoker.get();
         single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertFalse(RxScheduledExecutorService.used);
         Assert.assertEquals("x", value.get());
      }

      {
      latch = new CountDownLatch(1);
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(SingleRxInvokerProvider.class);
         SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
         Single<Response> single = invoker.get();
         single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
         boolean waitResult = latch.await(30, TimeUnit.SECONDS);
         Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
         Assert.assertTrue(RxScheduledExecutorService.used);
         Assert.assertEquals("x", value.get());
      }
   }

   @Test
   public void testUnhandledException() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/exception/unhandled")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = (Single<Thing>) invoker.get(Thing.class);
      single.subscribe(
            (Thing t) -> {},
            (Throwable t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Throwable t = unwrap((Throwable) value.get(), InternalServerErrorException.class);
      Assert.assertNotNull(t);
      Assert.assertTrue(t.getMessage().contains("500"));
   }

   @Test
   public void testHandledException() throws Exception {
      SingleRxInvoker invoker = client.target(generateURL("/exception/handled")).request().rx(SingleRxInvoker.class);
      Single<Thing> single = (Single<Thing>) invoker.get(Thing.class);
      single.subscribe(
            (Thing t) -> {},
            (Throwable t) -> {value.set(t); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Throwable t = unwrap((Throwable) value.get(), ClientErrorException.class);
      Assert.assertNotNull(t);
      Assert.assertTrue(t.getMessage().contains("444"));
   }

   @Test
   public void testGetTwoClients() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      ResteasyClient client1 = new ResteasyClientBuilder().build();
      client1.register(SingleRxInvokerProvider.class);
      SingleRxInvoker invoker1 = client1.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single1 = (Single<Response>) invoker1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(SingleRxInvokerProvider.class);
      SingleRxInvoker invoker2 = client2.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single2 = (Single<Response>) invoker2.get();

      single1.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});
      single2.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoInvokers() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      SingleRxInvoker invoker1 = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single1 = (Single<Response>) invoker1.get();

      SingleRxInvoker invoker2 = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single2 = (Single<Response>) invoker2.get();

      single1.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});
      single2.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoSingles() throws Exception {
      CountDownLatch cdl = new CountDownLatch(2);
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      SingleRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(SingleRxInvoker.class);
      Single<Response> single1 = (Single<Response>) invoker.get();
      Single<Response> single2 = (Single<Response>) invoker.get();

      single1.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});
      single2.subscribe((Response r) -> {list.add(r.readEntity(String.class)); cdl.countDown();});

      boolean waitResult = cdl.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
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
