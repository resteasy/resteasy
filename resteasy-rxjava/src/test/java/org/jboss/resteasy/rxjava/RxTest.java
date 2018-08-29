package org.jboss.resteasy.rxjava;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rx.Observable;
import rx.Single;

public class RxTest
{
   private static final Logger LOG = Logger.getLogger(RxTest.class);
   private static NettyJaxrsServer server;

   private static CountDownLatch latch;
   private static AtomicReference<Object> value = new AtomicReference<Object>();

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      List<Class> classes = server.getDeployment().getActualResourceClasses();
      classes.add(RxResource.class);
      List<Class> providers = server.getDeployment().getActualProviderClasses();
      providers.add(RxInjector.class);
      server.getDeployment().registration();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
   }


   private ResteasyClient client;

   @Before
   public void before()
   {
      client = new ResteasyClientBuilder()
            .readTimeout(5, TimeUnit.SECONDS)
            .connectionCheckoutTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)
            .build();
      value.set(null);
      latch = new CountDownLatch(1);
   }

   @After
   public void after()
   {
      client.close();
   }

   @Test
   public void testSingle() throws Exception
   {
      Single<Response> single = client.target(generateURL("/single")).request().rx(SingleRxInvoker.class).get();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      latch.await();
      assertEquals("got it", value.get());
   }
   
   @Test
   public void testSingleContext() throws Exception
   {
      Single<Response> single = client.target(generateURL("/context/single")).request().rx(SingleRxInvoker.class).get();
      single.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      latch.await();
      assertEquals("got it", value.get());
   }

   @Test
   @SuppressWarnings({ "unchecked", "deprecation" })
   public void testObservable() throws Exception
   {
      ObservableRxInvoker invoker = client.target(generateURL("/observable")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.get();
      List<String> data = new ArrayList<String>();
      observable.subscribe(
            (String s) -> data.add(s),
            (Throwable t) -> LOG.error(t.getMessage(), t),
            () -> latch.countDown());
      latch.await();
      assertArrayEquals(new String[] {"one", "two"}, data.toArray());
   }
   
   @Test
   @SuppressWarnings({ "unchecked", "deprecation" })
   public void testObservableContext() throws Exception
   {
      ObservableRxInvoker invoker = client.target(generateURL("/context/observable")).request().rx(ObservableRxInvoker.class);
      Observable<String> observable = (Observable<String>) invoker.get();
      List<String> data = new ArrayList<String>();
      observable.subscribe(
            (String s) -> data.add(s),
            (Throwable t) -> LOG.error(t.getMessage(), t),
            () -> latch.countDown());
      latch.await();
      assertArrayEquals(new String[] {"one", "two"}, data.toArray());
   }

//   @Test
   public void testInjection()
   {
      Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
      assertEquals((Integer)42, data);

      data = client.target(generateURL("/injection-async")).request().get(Integer.class);
      assertEquals((Integer)42, data);
   }
}