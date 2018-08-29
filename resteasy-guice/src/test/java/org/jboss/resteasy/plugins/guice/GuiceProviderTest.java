package org.jboss.resteasy.plugins.guice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;

public class GuiceProviderTest
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
      final TestResource resource = TestPortProvider.createProxy(TestResource.class);
      Assert.assertEquals("exception", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      String getName();
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

