package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.HashSet;
import java.util.Set;

public class ClientHeadersDefaultFactoryCDITest {

   private static UndertowJaxrsServer server;
   private static WeldContainer container;

   static class Worker {

      @Inject
      @RestClient
      private TestResourceIntf service;

      public String work() {
         return service.hello("Stefano");
      }
   }

   @Path("/")
   @RegisterRestClient(baseUri="http://localhost:8081")
   @RegisterClientHeaders
   @ClientHeaderParam(name="IntfHeader", value="intfValue")
   public interface TestResourceIntf {

      @Path("hello/{h}")
      @GET
      @ClientHeaderParam(name = "MthdHeader", value = "hello")
      String hello(@PathParam("h") String h);
   }

   @Path("/")
   public static class TestResource {

      @Path("hello/{h}")
      @GET
      public String hello(@PathParam("h") String h, @Context HttpHeaders httpHeaders) {
         return "IntfHeader: " + httpHeaders.getRequestHeader("IntfHeader").get(0)
                 + " - MthdHeader: " + httpHeaders.getRequestHeader("MthdHeader").get(0);
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
      weld.addBeanClass(TestResourceIntf.class);
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
      String result = container.select(Worker.class).get().work();
      Assert.assertTrue(result.contains("IntfHeader: intfValue"));
      Assert.assertTrue(result.contains("MthdHeader: hello"));
   }
}
