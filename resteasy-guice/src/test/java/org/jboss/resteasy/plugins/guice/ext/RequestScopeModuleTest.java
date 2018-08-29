package org.jboss.resteasy.plugins.guice.ext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provider;

public class RequestScopeModuleTest
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
            binder.bind(TestResource.class).to(RequestScopeTestResource.class);
         }
      };
      final ModuleProcessor processor = new ModuleProcessor(dispatcher.getRegistry(), dispatcher.getProviderFactory());
      processor.processInjector(Guice.createInjector(module, new RequestScopeModule()));
      final TestResource resource = TestPortProvider.createProxy(TestResource.class, TestPortProvider.generateBaseUrl());
      Assert.assertEquals("ok", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      String getName();
   }

   public static class RequestScopeTestResource implements TestResource
   {
      private final Request request;
      private final HttpHeaders httpHeaders;
      private final UriInfo uriInfo;
      private final SecurityContext securityContext;

      @Inject
      public RequestScopeTestResource(Request request,
            HttpHeaders httpHeaders, UriInfo uriInfo, SecurityContext securityContext)
      {
         this.request = request;
         this.httpHeaders = httpHeaders;
         this.uriInfo = uriInfo;
         this.securityContext = securityContext;
      }

      @Override
      public String getName()
      {
         Assert.assertNotNull(request);
         Assert.assertNotNull(httpHeaders);
         Assert.assertNotNull(uriInfo);
         Assert.assertNotNull(securityContext);
         return "ok";
      }
   }
   
   /**
    * Tests fix for RESTEASY-1428. Thanks to Antti Lampinen for this test.
    */
   @Test
   public void testToString()
   {
      Key<Injector> key = Key.get(Injector.class);
      Injector injector = Guice.createInjector(new RequestScopeModule());
      Provider<Injector> unscoped = injector.getProvider(key);
      Provider<Injector> scoped = injector.getScopeBindings().get(RequestScoped.class).scope(key, unscoped);
      scoped.toString(); // Fails on this line without fix.
   }
}
