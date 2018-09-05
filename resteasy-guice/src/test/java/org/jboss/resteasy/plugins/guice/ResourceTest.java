package org.jboss.resteasy.plugins.guice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

public class ResourceTest
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
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      server.stop();
      server = null;
      dispatcher = null;
   }

   @Test
   public void testResourceRegistered()
   {
      final Module module = new Module()
      {
         @Override
         public void configure(final Binder binder)
         {
            binder.bind(TestResource.class).to(TestResourceSimple.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.processInjector(Guice.createInjector(module));
      final TestResource resource = TestPortProvider.createProxy(TestResource.class, TestPortProvider.generateBaseUrl());
      Assert.assertEquals("name", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Test
   public void testResourceInjected()
   {
      final Module module = new Module()
      {
         @Override
         public void configure(final Binder binder)
         {
            binder.bind(String.class).toInstance("injected-name");
            binder.bind(TestResource.class).to(TestResourceInjected.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.processInjector(Guice.createInjector(module));
      final TestResource resource = TestPortProvider.createProxy(TestResource.class, TestPortProvider.generateBaseUrl());
      Assert.assertEquals("injected-name", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      String getName();
   }

   public static class TestResourceSimple implements TestResource
   {
      @Override
      public String getName()
      {
         return "name";
      }
   }

   public static class TestResourceInjected implements TestResource
   {
      private final String name;

      @Inject
      public TestResourceInjected(final String name)
      {
         this.name = name;
      }

      @Override
      public String getName()
      {
         return name;
      }
   }
}
