package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResource;
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
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * These tests run asynchronously on client, calling a proxy which calls a CompletionStageRxInvoker.
 * The server creates and returns CompletionStages which run asynchronously.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RxCompletionStageProxyTest {

   private static ResteasyClient client;
   private static RxCompletionStageResource proxy;

   private static List<Thing>  xThingList =  new ArrayList<Thing>();
   private static List<Thing>  aThingList =  new ArrayList<Thing>();

   static {
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(RxCompletionStageProxyTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
            + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, RxCompletionStageResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxCompletionStageProxyTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testGet() throws Exception {
      CompletionStage<String> completionStage = proxy.get();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testGetThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.getThing();
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testGetThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.getThingList();
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPut() throws Exception {
      CompletionStage<String> completionStage = proxy.put("a");
      Assert.assertEquals("a", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPutThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.putThing("a");
      Assert.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPutThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.putThingList("a");
      Assert.assertEquals(aThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPost() throws Exception {
      CompletionStage<String> completionStage = proxy.post("a");
      Assert.assertEquals("a", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPostThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.postThing("a");
      Assert.assertEquals(new Thing("a"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testPostThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.postThingList("a");
      Assert.assertEquals(aThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testDelete() throws Exception {
      CompletionStage<String> completionStage = proxy.delete();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testDeleteThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.deleteThing();
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testDeleteThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.deleteThingList();
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testHead() throws Exception {
      CompletionStage<String> completionStage = proxy.head();
      try {
         completionStage.toCompletableFuture().get();
      } catch (Exception e) {
         Assert.assertTrue(throwableContains(e, "Input stream was empty, there is no entity"));
      }
   }

   @Test
   public void testOptions() throws Exception {
      CompletionStage<String> completionStage = proxy.options();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get());
   }

   @Test
   public void testOptionsThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.optionsThing();
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   public void testOptionsThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.optionsThingList();
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      CompletionStage<String> completionStage = proxy.trace();
      Assert.assertEquals("x", completionStage.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      CompletionStage<Thing> completionStage = proxy.traceThing();
      Assert.assertEquals(new Thing("x"), completionStage.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      CompletionStage<List<Thing>> completionStage = proxy.traceThingList();
      Assert.assertEquals(xThingList, completionStage.toCompletableFuture().get());
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         CompletionStage<String> completionStage = proxy.get();
         Assert.assertEquals("x", completionStage.toCompletableFuture().get());
         Assert.assertFalse(RxScheduledExecutorService.used);
      }

      {
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(CompletionStageRxInvokerProvider.class);
         RxCompletionStageResource proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
         CompletionStage<String> completionStage = proxy.get();
         Assert.assertEquals("x", completionStage.toCompletableFuture().get());
         Assert.assertTrue(RxScheduledExecutorService.used);
      }
   }

   @Test
   public void testUnhandledException() throws Exception {
      CompletionStage<Thing> completionStage = proxy.exceptionUnhandled();
      AtomicReference<Throwable> value = new AtomicReference<Throwable>();
      CountDownLatch latch = new CountDownLatch(1);
      completionStage.whenComplete((Thing t1, Throwable t2) -> {value.set(t2); latch.countDown();});
      boolean waitResult = latch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Waiting for event to be delivered has timed out.", waitResult);
      Assert.assertTrue(value.get().getMessage().contains("500"));
   }

   @Test
   public void testHandledException() throws Exception {
      CompletionStage<Thing> completionStage = proxy.exceptionHandled();
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
      RxCompletionStageResource  proxy1 = client1.target(generateURL("/")).proxy(RxCompletionStageResource.class);
      CompletionStage<String> completionStage1 = proxy1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(CompletionStageRxInvokerProvider.class);
      RxCompletionStageResource  proxy2 = client2.target(generateURL("/")).proxy(RxCompletionStageResource.class);
      CompletionStage<String> completionStage2 = proxy2.get();

      list.add(completionStage1.toCompletableFuture().get());
      list.add(completionStage2.toCompletableFuture().get());
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++)
      {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoProxies() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      RxCompletionStageResource  proxy1 = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
      CompletionStage<String> completionStage1 = proxy1.get();

      RxCompletionStageResource  proxy2 = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
      CompletionStage<String> completionStage2 = proxy2.get();

      list.add(completionStage1.toCompletableFuture().get());
      list.add(completionStage2.toCompletableFuture().get());
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++)
      {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoCompletionStages() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      CompletionStage<String> completionStage1 = proxy.get();
      CompletionStage<String> completionStage2 = proxy.get();

      list.add(completionStage1.toCompletableFuture().get());
      list.add(completionStage2.toCompletableFuture().get());
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++)
      {
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
