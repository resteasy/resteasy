package org.jboss.resteasy.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorTest
{
   private static NettyJaxrsServer server;

   private static CountDownLatch latch;
   private static AtomicReference<Object> value = new AtomicReference<Object>();
   private static final Logger LOG = Logger.getLogger(NettyJaxrsServer.class);

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      List<Class> classes = server.getDeployment().getActualResourceClasses();
      classes.add(ReactorResource.class);
      List<Class> providers = server.getDeployment().getActualProviderClasses();
      providers.add(ReactorInjector.class);
      server.getDeployment().start();
      server.getDeployment().registration();
      server.start();
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
      client = ((ResteasyClientBuilder)ClientBuilder.newBuilder())
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
   public void testMono() throws Exception {
      Mono<Response> mono = client.target(generateURL("/mono")).request().rx(MonoRxInvoker.class).get();
      mono.subscribe((Response r) -> {value.set(r.readEntity(String.class)); latch.countDown();});
      latch.await();
      assertEquals("got it", value.get());
   }

   @Test
   public void testFlux() throws Exception {
      FluxRxInvoker invoker = client.target(generateURL("/flux")).request().rx(FluxRxInvoker.class);
      @SuppressWarnings("unchecked")
      Flux<String> flux = (Flux<String>) invoker.get();
      List<String> data = new ArrayList<>();
      flux.subscribe(
         (String s) -> data.add(s),
         (Throwable t) -> LOG.error(t.getMessage(), t),
         () -> latch.countDown());
      latch.await();
      assertArrayEquals(new String[] {"one", "two"}, data.toArray());
   }

   @Test
   public void testInjection()
   {
      Integer data = client.target(generateURL("/injection")).request().get(Integer.class);
      assertEquals((Integer)42, data);

      data = client.target(generateURL("/injection-async")).request().get(Integer.class);
      assertEquals((Integer)42, data);
   }

}
