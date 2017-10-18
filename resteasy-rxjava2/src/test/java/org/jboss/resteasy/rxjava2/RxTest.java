package org.jboss.resteasy.rxjava2;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RxTest
{
   private static NettyJaxrsServer server;

   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      dispatcher = server.getDeployment().getDispatcher();
      POJOResourceFactory noDefaults = new POJOResourceFactory(RxResource.class);
      dispatcher.getRegistry().addResourceFactory(noDefaults);
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
      dispatcher = null;
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
   }

   @After
   public void after()
   {
      client.close();
   }

   @Test
   public void testSingle()
   {
      String data = client.target(generateURL("/single")).request().get(String.class);
      assertEquals("got it", data);

      String[] data2 = client.target(generateURL("/observable")).request().get(String[].class);
      assertArrayEquals(new String[] {"one", "two"}, data2);

      data2 = client.target(generateURL("/flowable")).request().get(String[].class);
      assertArrayEquals(new String[] {"one", "two"}, data2);

      data = client.target(generateURL("/context/single")).request().get(String.class);
      assertEquals("got it", data);

      data2 = client.target(generateURL("/context/observable")).request().get(String[].class);
      assertArrayEquals(new String[] {"one", "two"}, data2);

      data2 = client.target(generateURL("/context/flowable")).request().get(String[].class);
      assertArrayEquals(new String[] {"one", "two"}, data2);
   }
}