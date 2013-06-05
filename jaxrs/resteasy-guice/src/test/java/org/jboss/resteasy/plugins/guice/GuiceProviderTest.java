package org.jboss.resteasy.plugins.guice;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GuiceProviderTest
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
   public void testProvider()
   {
      final Module module = new Module()
      {
         @Override
         public void configure(final Binder binder)
         {
            binder.bind(TestExceptionProvider.class);
            binder.bind(TestResource.class).to(TestResourceException.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.processInjector(Guice.createInjector(module));
      final TestResource resource = ProxyFactory.create(TestResource.class, generateBaseUrl());
      Assert.assertEquals("exception", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      public String getName();
   }

   public static class TestResourceException implements TestResource
   {
      @Override
      public String getName()
      {
         throw new TestException();
      }
   }

   public static class TestException extends RuntimeException
   {
   }

   @Provider
   public static class TestExceptionProvider implements ExceptionMapper<TestException>
   {
      @Override
      public Response toResponse(final TestException exception)
      {
         return Response.ok("exception").build();
      }
   }
}
