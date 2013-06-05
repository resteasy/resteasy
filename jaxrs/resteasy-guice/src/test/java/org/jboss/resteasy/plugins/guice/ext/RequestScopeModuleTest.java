package org.jboss.resteasy.plugins.guice.ext;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Module;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.guice.ModuleProcessor;
import org.jboss.resteasy.test.EmbeddedContainer;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RequestScopeModuleTest
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
      final TestResource resource = ProxyFactory.create(TestResource.class, generateBaseUrl());
      Assert.assertEquals("ok", resource.getName());
      dispatcher.getRegistry().removeRegistrations(TestResource.class);
   }

   @Path("test")
   public interface TestResource
   {
      @GET
      public String getName();
   }

   public static class RequestScopeTestResource implements TestResource
   {
      private final HttpServletRequest httpServletRequest;
      private final HttpServletResponse httpServletResponse;
      private final Request request;
      private final HttpHeaders httpHeaders;
      private final UriInfo uriInfo;
      private final SecurityContext securityContext;

      @Inject
      public RequestScopeTestResource(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse, Request request,
            HttpHeaders httpHeaders, UriInfo uriInfo, SecurityContext securityContext)
      {
         this.httpServletRequest = httpServletRequest;
         this.httpServletResponse = httpServletResponse;
         this.request = request;
         this.httpHeaders = httpHeaders;
         this.uriInfo = uriInfo;
         this.securityContext = securityContext;
      }

      @Override
      public String getName()
      {
         Assert.assertNotNull(httpServletRequest);
         Assert.assertNotNull(httpServletResponse);
         Assert.assertNotNull(request);
         Assert.assertNotNull(httpHeaders);
         Assert.assertNotNull(uriInfo);
         Assert.assertNotNull(securityContext);
         return "ok";
      }
   }
}
