package org.jboss.resteasy.plugins.guice;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static org.jboss.resteasy.test.TestPortProvider.*;

public class ResourceTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
   }

   @AfterClass
   public static void afterClass() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testResourceRegistered()
   {
      final Module module = new Module()
      {
         public void configure(final Binder binder)
         {
            binder.bind(TestResource.class).to(TestResourceSimple.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.process(module);
      final TestResource resource = ProxyFactory.create(TestResource.class, generateBaseUrl());
      Assert.assertEquals("name", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Test
   public void testResourceInjected()
   {
      final Module module = new Module()
      {
         public void configure(final Binder binder)
         {
            binder.bind(String.class).toInstance("injected-name");
            binder.bind(TestResource.class).to(TestResourceInjected.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.process(module);
      final TestResource resource = ProxyFactory.create(TestResource.class, generateBaseUrl());
      Assert.assertEquals("injected-name", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      public String getName();
   }

   public static class TestResourceSimple implements TestResource
   {
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

      public String getName()
      {
         return name;
      }
   }
}
