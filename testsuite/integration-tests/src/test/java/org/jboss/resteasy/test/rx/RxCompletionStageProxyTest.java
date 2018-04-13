package org.jboss.resteasy.test.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.rxjava.SingleRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResource;
import org.jboss.resteasy.test.rx.resource.RxCompletionStageResourceImpl;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.Thing;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
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
 * @tpSince RESTEasy 4.0
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
      war.addAsLibrary(TestUtil.resolveDependency("io.reactivex:rxjava:1.3.2"));
      war.addAsLibrary(TestUtil.resolveDependency("org.jboss.resteasy:resteasy-rxjava:4.0.0-SNAPSHOT"));
      return TestUtil.finishContainerPrepare(war, null, RxCompletionStageResourceImpl.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, RxCompletionStageProxyTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      client.register(SingleRxInvokerProvider.class);
      proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////
   @Test
   public void testGet() throws Exception {
      CompletionStage<String> single = proxy.get();
      Assert.assertEquals("x", single.toCompletableFuture().get());
   }

   @Test
   public void testGetThing() throws Exception {
      CompletionStage<Thing> single = proxy.getThing();
      Assert.assertEquals(new Thing("x"), single.toCompletableFuture().get());
   }

   @Test
   public void testGetThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.getThingList();
      Assert.assertEquals(xThingList, single.toCompletableFuture().get());
   }

   @Test
   public void testPut() throws Exception {
      CompletionStage<String> single = proxy.put("a");
      Assert.assertEquals("a", single.toCompletableFuture().get());
   }

   @Test
   public void testPutThing() throws Exception {
      CompletionStage<Thing> single = proxy.putThing("a");
      Assert.assertEquals(new Thing("a"), single.toCompletableFuture().get());
   }

   @Test
   public void testPutThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.putThingList("a");
      Assert.assertEquals(aThingList, single.toCompletableFuture().get());
   }

   @Test
   public void testPost() throws Exception {
      CompletionStage<String> single = proxy.post("a");
      Assert.assertEquals("a", single.toCompletableFuture().get());
   }

   @Test
   public void testPostThing() throws Exception {
      CompletionStage<Thing> single = proxy.postThing("a");
      Assert.assertEquals(new Thing("a"), single.toCompletableFuture().get());
   }

   @Test
   public void testPostThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.postThingList("a");
      Assert.assertEquals(aThingList, single.toCompletableFuture().get());
   }

   @Test
   public void testDelete() throws Exception {
      CompletionStage<String> single = proxy.delete();
      Assert.assertEquals("x", single.toCompletableFuture().get());
   }

   @Test
   public void testDeleteThing() throws Exception {
      CompletionStage<Thing> single = proxy.deleteThing();
      Assert.assertEquals(new Thing("x"), single.toCompletableFuture().get());
   }

   @Test
   public void testDeleteThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.deleteThingList();
      Assert.assertEquals(xThingList, single.toCompletableFuture().get());
   }

   @Test
   public void testHead() throws Exception {
      CompletionStage<String> single = proxy.head();
      try {
         single.toCompletableFuture().get();
      } catch (Exception e) {
         Assert.assertTrue(throwableContains(e, "Input stream was empty, there is no entity"));
      }
   }

   @Test
   public void testOptions() throws Exception {
      CompletionStage<String> single = proxy.options();
      Assert.assertEquals("x", single.toCompletableFuture().get());
   }

   @Test
   public void testOptionsThing() throws Exception {
      CompletionStage<Thing> single = proxy.optionsThing();
      Assert.assertEquals(new Thing("x"), single.toCompletableFuture().get());
   }

   @Test
   public void testOptionsThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.optionsThingList();
      Assert.assertEquals(xThingList, single.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      CompletionStage<String> single = proxy.trace();
      Assert.assertEquals("x", single.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      CompletionStage<Thing> single = proxy.traceThing();
      Assert.assertEquals(new Thing("x"), single.toCompletableFuture().get());
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      CompletionStage<List<Thing>> single = proxy.traceThingList();
      Assert.assertEquals(xThingList, single.toCompletableFuture().get());
   }

   @Test
   public void testScheduledExecutorService () throws Exception {
      {
         RxScheduledExecutorService.used = false;
         CompletionStage<String> single = proxy.get();
         Assert.assertEquals("x", single.toCompletableFuture().get());
         Assert.assertFalse(RxScheduledExecutorService.used);
      }

      {
         RxScheduledExecutorService.used = false;
         RxScheduledExecutorService executor = new RxScheduledExecutorService();
         ResteasyClient client = ((ResteasyClientBuilder) new ResteasyClientBuilder().executorService(executor)).build();
         client.register(SingleRxInvokerProvider.class);
         RxCompletionStageResource proxy = client.target(generateURL("/")).proxy(RxCompletionStageResource.class);
         CompletionStage<String> single = proxy.get();
         Assert.assertEquals("x", single.toCompletableFuture().get());
         Assert.assertTrue(RxScheduledExecutorService.used);
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