package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForBootableJar;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
import org.jboss.resteasy.test.rx.resource.SimpleResource;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * In these tests, the server creates and returns a Single<T>.
 * The client uses a proxy to do a synchronous invocation to get an object of type T.
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category(NotForBootableJar.class) // no RX layer so far
public class Rx2SingleProxyServerAsyncTest {

   private static ResteasyClient client;
   private static SimpleResource proxy;

   private static List<Thing>  xThingList =  new ArrayList<Thing>();
   private static List<Thing>  aThingList =  new ArrayList<Thing>();

   static {
      for (int i = 0; i < 3; i++) {xThingList.add(new Thing("x"));}
      for (int i = 0; i < 3; i++) {aThingList.add(new Thing("a"));}
   }

   @Deployment
   public static Archive<?> deploy() {
      WebArchive war = TestUtil.prepareArchive(Rx2SingleProxyServerAsyncTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, Rx2SingleResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Rx2SingleProxyServerAsyncTest.class.getSimpleName());
   }

   //////////////////////////////////////////////////////////////////////////////
   @BeforeClass
   public static void beforeClass() throws Exception {
      client = new ResteasyClientBuilder().build();
      proxy = client.target(generateURL("/")).proxy(SimpleResource.class);
   }

   @AfterClass
   public static void after() throws Exception {
      client.close();
   }

   //////////////////////////////////////////////////////////////////////////////

   @Test
   public void testGet() throws Exception {
      String s = proxy.get();
      Assert.assertEquals("x", s);
   }

   @Test
   public void testGetThing() throws Exception {
      Thing t = proxy.getThing();
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testGetThingList() throws Exception {
      List<Thing> list = proxy.getThingList();
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testPut() throws Exception {
      String s = proxy.put("a");
      Assert.assertEquals("a", s);
   }

   @Test
   public void testPutThing() throws Exception {
      Thing t = proxy.putThing("a");
      Assert.assertEquals(new Thing("a"), t);
   }

   @Test
   public void testPutThingList() throws Exception {
      List<Thing> list = proxy.putThingList("a");
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testPost() throws Exception {
      String s = proxy.post("a");
      Assert.assertEquals("a", s);
   }

   @Test
   public void testPostThing() throws Exception {
      Thing t = proxy.postThing("a");
      Assert.assertEquals(new Thing("a"), t);
   }

   @Test
   public void testPostThingList() throws Exception {
      List<Thing> list = proxy.postThingList("a");
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testDelete() throws Exception {
      String s = proxy.delete();
      Assert.assertEquals("x", s);
   }

   @Test
   public void testDeleteThing() throws Exception {
      Thing t = proxy.deleteThing();
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testDeleteThingList() throws Exception {
      List<Thing> list = proxy.deleteThingList();
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testHead() throws Exception {
      try {
         proxy.head();
      } catch (Exception e) {
         Assert.assertTrue(throwableContains(e, "Input stream was empty, there is no entity"));
      }
   }

   @Test
   public void testOptions() throws Exception {
      String s = proxy.options();
      Assert.assertEquals("x", s);
   }

   @Test
   public void testOptionsThing() throws Exception {
      Thing t = proxy.optionsThing();
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testOptionsThingList() throws Exception {
      List<Thing> list = proxy.optionsThingList();
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testUnhandledException() throws Exception {
      try {
         proxy.getThing();
      } catch (Exception e)
      {
         Assert.assertTrue(e.getMessage().contains("500"));
      }
   }

   @Test
   public void testHandledException() throws Exception {
      try {
         proxy.getThing();
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage().contains("444"));
      }
   }

   @Test
   public void testGetTwoClients() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      ResteasyClient client1 = new ResteasyClientBuilder().build();
      client1.register(CompletionStageRxInvokerProvider.class);
      SimpleResource proxy1 = client1.target(generateURL("/")).proxy(SimpleResource.class);
      String s1 = proxy1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(CompletionStageRxInvokerProvider.class);
      SimpleResource  proxy2 = client2.target(generateURL("/")).proxy(SimpleResource.class);
      String s2 = proxy2.get();

      list.add(s1);
      list.add(s2);
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++)
      {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoProxies() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      SimpleResource  proxy1 = client.target(generateURL("/")).proxy(SimpleResource.class);
      String s1 = proxy1.get();

      SimpleResource  proxy2 = client.target(generateURL("/")).proxy(SimpleResource.class);
      String s2 = proxy2.get();

      list.add(s1);
      list.add(s2);
      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++)
      {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoCompletionStages() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      String s1 = proxy.get();
      String s2 = proxy.get();

      list.add(s1);
      list.add(s2);
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
