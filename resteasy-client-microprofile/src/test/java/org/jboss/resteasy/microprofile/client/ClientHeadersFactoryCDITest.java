package org.jboss.resteasy.microprofile.client;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientHeadersFactoryCDITest {

   private static UndertowJaxrsServer server;
   private static WeldContainer container;

   static class Worker {

      @Inject
      @RestClient
      private SubClassResourceIntf service;

      public String work() {
         return service.hello("Stefano");
      }
   }

   @Path("/")
   public interface TestResourceIntf {

      @Path("hello/{h}")
      @GET
      String hello(@PathParam("h") String h);
   }

   @RegisterRestClient(baseUri="http://localhost:8081")
   @RegisterClientHeaders(TestClientHeadersFactory.class)
   @ClientHeaderParam(name="IntfHeader", value="intfValue")
   public interface SubClassResourceIntf extends TestResourceIntf {};

   @Path("/")
   public static class TestResource {

      @Path("hello/{h}")
      @GET
      public String hello(@PathParam("h") String h) {
         return "hello " + h;
      }
   }

   @ApplicationScoped
   public static class TestClientHeadersFactory implements ClientHeadersFactory {

      @Inject
      private Counter counter;

      public MultivaluedMap<String, String> update(MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders) {
         counter.count();
         return new MultivaluedHashMap<>();
      }
   }

   @ApplicationScoped
   public static class Counter {

       public static final AtomicInteger COUNT = new AtomicInteger(0);

       public int count() {
           return COUNT.incrementAndGet();
       }
   }

   @ApplicationPath("")
   public static class MyApp extends Application {

      @Override
      public Set<Class<?>> getClasses() {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         return classes;
      }
   }

   @BeforeClass
   public static void init() throws Exception {
      Weld weld = new Weld();
      weld.addBeanClass(Worker.class);
      weld.addBeanClass(SubClassResourceIntf.class);
      weld.addBeanClass(TestClientHeadersFactory.class);
      weld.addBeanClass(Counter.class);
      container = weld.initialize();
      server = new UndertowJaxrsServer().start();
      server.deploy(MyApp.class);
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
      container.shutdown();
   }

   @Test
   public void test() {
      Assert.assertTrue(container.isRunning());
      String result = container.select(Worker.class).get().work();
      Assert.assertEquals("hello Stefano", result);
      Assert.assertEquals(1, Counter.COUNT.get());
   }
}
