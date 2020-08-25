package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.client.CompletionStageRxInvoker;
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
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResourceImpl;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.TestException;
import org.jboss.resteasy.test.rx.resource.TestExceptionMapper;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * These tests run asynchronously on client, calling a CompletionStageRxInvoker.
 * The server creates and returns CompletionStages which run asynchronously.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class RxCompletionStageTest {

   private static ResteasyClient client;

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
      WebArchive war = TestUtil.prepareArchive(RxCompletionStageTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
            + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, RxCompletionStageResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxCompletionStageTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.get();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testGetString() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<String> completionStage = invoker.get(String.class);
      Assert.assertEquals("x", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testGetThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.get(Thing.class);
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testGetThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.get(LIST_OF_THING);
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPut() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/put/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.put(aEntity);
      Assert.assertEquals("a", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testPutThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/put/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.put(aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPutThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/put/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.put(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPost() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.post(aEntity);
      Assert.assertEquals("a", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testPostThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.post(aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPostThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.post(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testDelete() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/delete/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.delete();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testDeleteThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/delete/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.delete(Thing.class);
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testDeleteThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/delete/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.delete(LIST_OF_THING);
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testHead() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/head/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.head();
      Response response = completionStage.toCompletableFuture().get();
      Assert.assertEquals(200, response.getStatus());
   }

   @Test
   public void testOptions() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/options/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.options();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testOptionsThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/options/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.options(Thing.class);
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testOptionsThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/options/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.options(LIST_OF_THING);
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/trace/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.trace();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/trace/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.trace(Thing.class);
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/trace/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.trace(LIST_OF_THING);
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testMethodGet() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.method("GET");
      Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testMethodGetThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.method("GET", Thing.class);
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testMethodGetThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/get/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.method("GET", LIST_OF_THING);
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testMethodPost() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage = invoker.method("POST", aEntity);
      Assert.assertEquals("a", completionStage.toCompletableFuture().get().readEntity(String.class));
   }

   @Test
   public void testMethodPostThing() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/thing")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = invoker.method("POST", aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testMethodPostThingList() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/post/thing/list")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<List<Thing>> completionStage = invoker.method("POST", aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
         CompletionStage<Response> completionStage = invoker.get();
         Assert.assertFalse(RxScheduledExecutorService.used);
         Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
      }

      {
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(CompletionStageRxInvokerProvider.class);
         CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
         CompletionStage<Response> completionStage = invoker.get();
         Assert.assertTrue(RxScheduledExecutorService.used);
         Assert.assertEquals("x", completionStage.toCompletableFuture().get().readEntity(String.class));
      }
   }

   @Test
   public void testUnhandledException() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/exception/unhandled")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = (CompletionStage<Thing>) invoker.get(Thing.class);
      AtomicReference<Throwable> value = new AtomicReference<Throwable>();
      CountDownLatch latch = new CountDownLatch(1);
      completionStage.whenComplete((Thing t1, Throwable t2) -> {value.set(t2); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertTrue(value.get().getMessage().contains("500"));
   }

   @Test
   public void testHandledException() throws Exception {
      CompletionStageRxInvoker invoker = client.target(generateURL("/exception/handled")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Thing> completionStage = (CompletionStage<Thing>) invoker.get(Thing.class);
      AtomicReference<Throwable> value = new AtomicReference<Throwable>();
      CountDownLatch latch = new CountDownLatch(1);
      completionStage.whenComplete((Thing t1, Throwable t2) -> {value.set(t2); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertTrue(value.get().getMessage().contains("444"));
   }

   @Test
   public void testGetTwoClients() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      ResteasyClient client1 = new ResteasyClientBuilder().build();
      client1.register(CompletionStageRxInvokerProvider.class);
      CompletionStageRxInvoker invoker1 = client1.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage1 = (CompletionStage<Response>) invoker1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(CompletionStageRxInvokerProvider.class);
      CompletionStageRxInvoker invoker2 = client2.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage2 = (CompletionStage<Response>) invoker2.get();

      list.add(completionStage1.toCompletableFuture().get().readEntity(String.class));
      list.add(completionStage2.toCompletableFuture().get().readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoInvokers() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      CompletionStageRxInvoker invoker1 = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage1 = (CompletionStage<Response>) invoker1.get();

      CompletionStageRxInvoker invoker2 = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage2 = (CompletionStage<Response>) invoker2.get();

      list.add(completionStage1.toCompletableFuture().get().readEntity(String.class));
      list.add(completionStage2.toCompletableFuture().get().readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoCompletionStages() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      CompletionStageRxInvoker invoker = client.target(generateURL("/get/string")).request().rx(CompletionStageRxInvoker.class);
      CompletionStage<Response> completionStage1 = (CompletionStage<Response>) invoker.get();
      CompletionStage<Response> completionStage2 = (CompletionStage<Response>) invoker.get();

      list.add(completionStage1.toCompletableFuture().get().readEntity(String.class));
      list.add(completionStage2.toCompletableFuture().get().readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }
}
