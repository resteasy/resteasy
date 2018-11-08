package org.jboss.resteasy.test.rx.rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerProvider;
import org.jboss.resteasy.test.rx.resource.RxScheduledExecutorService;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * @tpSubChapter Reactive classes
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.6
 *
 * In these tests, the server resource methods create and return objects of type Single<T>.
 * The client does synchronous invocations to get objects of type T.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class Rx2SingleServerAsyncTest {

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
      WebArchive war = TestUtil.prepareArchive(Rx2SingleServerAsyncTest.class.getSimpleName());
      war.addClass(Thing.class);
      war.addClass(RxScheduledExecutorService.class);
      war.addClass(TestException.class);
      war.setManifest(new StringAsset("Manifest-Version: 1.0\n"
         + "Dependencies: org.jboss.resteasy.resteasy-rxjava2 services\n"));
      return TestUtil.finishContainerPrepare(war, null, Rx2SingleResourceImpl.class, TestExceptionMapper.class);
   }

   private static String generateURL(String path) {
      return PortProviderUtil.generateURL(path, Rx2SingleServerAsyncTest.class.getSimpleName());
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
      Builder request = client.target(generateURL("/get/string")).request();
      Response response = request.get();
      Assert.assertEquals("x", response.readEntity(String.class));
   }

   @Test
   public void testGetString() throws Exception {
      Builder request = client.target(generateURL("/get/string")).request();
      String s = request.get(String.class);
      Assert.assertEquals("x", s);
   }

   @Test
   public void testGetThing() throws Exception {
      Builder request = client.target(generateURL("/get/thing")).request();
      Thing t = request.get(Thing.class);
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testGetThingList() throws Exception {
      Builder request = client.target(generateURL("/get/thing/list")).request();
      List<Thing> list = request.get(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testPut() throws Exception {
      Builder request = client.target(generateURL("/put/string")).request();
      Response response = request.put(aEntity);
      Assert.assertEquals("a", response.readEntity(String.class));
   }

   @Test
   public void testPutThing() throws Exception {
      Builder request = client.target(generateURL("/put/thing")).request();
      Thing t = request.put(aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), t);
   }

   @Test
   public void testPutThingList() throws Exception {
      Builder request = client.target(generateURL("/put/thing/list")).request();
      List<Thing> list = request.put(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testPost() throws Exception {
      Builder request = client.target(generateURL("/post/string")).request();
      Response response = request.post(aEntity);
      Assert.assertEquals("a", response.readEntity(String.class));
   }

   @Test
   public void testPostThing() throws Exception {
      Builder request = client.target(generateURL("/post/thing")).request();
      Thing t = request.post(aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), t);
   }

   @Test
   public void testPostThingList() throws Exception {
      Builder request = client.target(generateURL("/post/thing/list")).request();
      List<Thing> list = request.post(aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testDelete() throws Exception {
      Builder request = client.target(generateURL("/delete/string")).request();
      Response response = request.delete();
      Assert.assertEquals("x", response.readEntity(String.class));
   }

   @Test
   public void testDeleteThing() throws Exception {
      Builder request = client.target(generateURL("/delete/thing")).request();
      Thing t = request.delete(Thing.class);
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testDeleteThingList() throws Exception {
      Builder request = client.target(generateURL("/delete/thing/list")).request();
      List<Thing> list = request.delete(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testHead() throws Exception {
      Builder request = client.target(generateURL("/head/string")).request();
      Response response = request.head();
      Assert.assertEquals(200, response.getStatus());
   }

   @Test
   public void testOptions() throws Exception {
      Builder request = client.target(generateURL("/options/string")).request();
      Response response = request.options();
      Assert.assertEquals("x", response.readEntity(String.class));
   }

   @Test
   public void testOptionsThing() throws Exception {
      Builder request = client.target(generateURL("/options/thing")).request();
      Thing t = request.options(Thing.class);
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testOptionsThingList() throws Exception {
      Builder request = client.target(generateURL("/options/thing/list")).request();
      List<Thing> list = request.options(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTrace() throws Exception {
      Builder request = client.target(generateURL("/trace/string")).request();
      Response response = request.trace();
      Assert.assertEquals("x", response.readEntity(String.class));
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThing() throws Exception {
      Builder request = client.target(generateURL("/trace/thing")).request();
      Thing t = request.trace(Thing.class);
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   @Ignore // TRACE is disabled by default in Wildfly
   public void testTraceThingList() throws Exception {
      Builder request = client.target(generateURL("/trace/thing/list")).request();
      List<Thing> list = request.trace(LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testMethodGet() throws Exception {
      Builder request = client.target(generateURL("/get/string")).request();
      Response response = request.method("GET");
      Assert.assertEquals("x", response.readEntity(String.class));
   }

   @Test
   public void testMethodGetThing() throws Exception {
      Builder request = client.target(generateURL("/get/thing")).request();
      Thing t = request.method("GET", Thing.class);
      Assert.assertEquals(new Thing("x"), t);
   }

   @Test
   public void testMethodGetThingList() throws Exception {
      Builder request = client.target(generateURL("/get/thing/list")).request();
      List<Thing> list = request.method("GET", LIST_OF_THING);
      Assert.assertEquals(xThingList, list);
   }

   @Test
   public void testMethodPost() throws Exception {
      Builder request = client.target(generateURL("/post/string")).request();
      Response response = request.method("POST", aEntity);
      Assert.assertEquals("a", response.readEntity(String.class));
   }

   @Test
   public void testMethodPostThing() throws Exception {
      Builder request = client.target(generateURL("/post/thing")).request();
      Thing t = request.method("POST", aEntity, Thing.class);
      Assert.assertEquals(new Thing("a"), t);
   }

   @Test
   public void testMethodPostThingList() throws Exception {
      Builder request = client.target(generateURL("/post/thing/list")).request();
      List<Thing> list = request.method("POST", aEntity, LIST_OF_THING);
      Assert.assertEquals(aThingList, list);
   }

   @Test
   public void testUnhandledException() throws Exception {
      Builder request = client.target(generateURL("/exception/unhandled")).request();
      try {
         request.get(Thing.class);
      } catch (Exception e)
      {
         Assert.assertTrue(e.getMessage().contains("500"));
      }
   }

   @Test
   public void testHandledException() throws Exception {
      Builder request = client.target(generateURL("/exception/handled")).request();
      try {
         request.get(Thing.class);
      } catch (Exception e) {
         Assert.assertTrue(e.getMessage().contains("444"));
      }
   }

   @Test
   public void testGetTwoClients() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      ResteasyClient client1 = new ResteasyClientBuilder().build();
      client1.register(CompletionStageRxInvokerProvider.class);
      Builder request1 = client1.target(generateURL("/get/string")).request();
      Response response1 = request1.get();

      ResteasyClient client2 = new ResteasyClientBuilder().build();
      client2.register(CompletionStageRxInvokerProvider.class);
      Builder request2 = client2.target(generateURL("/get/string")).request();
      Response response2 = request2.get();

      list.add(response1.readEntity(String.class));
      list.add(response2.readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoInvokers() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      Builder request1 = client.target(generateURL("/get/string")).request();
      Response response1 = request1.get();

      Builder request2 = client.target(generateURL("/get/string")).request();
      Response response2 = request2.get();

      list.add(response1.readEntity(String.class));
      list.add(response2.readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }

   @Test
   public void testGetTwoCompletionStages() throws Exception {
      CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();

      Builder request = client.target(generateURL("/get/string")).request();
      Response response1 = request.get();
      Response response2 = request.get();

      list.add(response1.readEntity(String.class));
      list.add(response2.readEntity(String.class));

      Assert.assertEquals(2, list.size());
      for (int i = 0; i < 2; i++) {
         Assert.assertEquals("x", list.get(i));
      }
   }
}
