package org.jboss.resteasy.plugins.guice.ext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
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

public class JaxrsModuleTest
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
   public void testInjection()
   {
      final Module module = new Module()
      {
         @Override
         public void configure(final Binder binder)
         {
            binder.bind(TestResource.class).to(JaxrsTestResource.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.processInjector(Guice.createInjector(module, new JaxrsModule()));
      final TestResource resource = TestPortProvider.createProxy(TestResource.class);
      Assert.assertEquals("ok", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      String getName();
   }

   public static class JaxrsTestResource implements TestResource
   {
      private final ClientHttpEngine clientExecutor;
      private final RuntimeDelegate runtimeDelegate;
      private final Response.ResponseBuilder responseBuilder;
      private final UriBuilder uriBuilder;
      private final Variant.VariantListBuilder variantListBuilder;

      @Inject
      public JaxrsTestResource(final ClientHttpEngine clientExecutor, final RuntimeDelegate runtimeDelegate, final Response.ResponseBuilder responseBuilder, final UriBuilder uriBuilder, final Variant.VariantListBuilder variantListBuilder)
      {
         this.clientExecutor = clientExecutor;
         this.runtimeDelegate = runtimeDelegate;
         this.responseBuilder = responseBuilder;
         this.uriBuilder = uriBuilder;
         this.variantListBuilder = variantListBuilder;
      }

      @Override
      public String getName()
      {
         Assert.assertNotNull(clientExecutor);
         Assert.assertNotNull(runtimeDelegate);
         Assert.assertNotNull(responseBuilder);
         Assert.assertNotNull(uriBuilder);
         Assert.assertNotNull(variantListBuilder);
         return "ok";
      }
   }
}

