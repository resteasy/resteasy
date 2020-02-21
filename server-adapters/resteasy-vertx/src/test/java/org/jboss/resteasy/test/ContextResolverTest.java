package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.plugins.server.vertx.VertxResteasyDeployment;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Test that dynamic feature doesn't add to all resource methods
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContextResolverTest
{
   public static class HolderClass {
      public static final String OK = "11111";

      private HttpHeaders headers;

      private UriInfo info;

      private Application application;

      private Request request;

      private Providers provider;

      public HolderClass(HttpHeaders headers, UriInfo info, Application application,
                         Request request, Providers provider) {
         super();
         this.headers = headers;
         this.info = info;
         this.application = application;
         this.request = request;
         this.provider = provider;
      }

      public Response toResponse() {
         int ok = application != null ? 1 : 0;
         ok += headers != null ? 10 : 0;
         ok += info != null ? 100 : 0;
         ok += request != null ? 1000 : 0;
         ok += provider == null ? 10000 : 0;
         return Response.ok(String.valueOf(ok)).build();
      }
   }

   @Provider
   public static class HolderResolver implements ContextResolver<HolderClass> {
      private HttpHeaders headers;

      private UriInfo info;

      private Application application;

      private Request request;

      private Providers provider;

      protected HolderResolver(@Context HttpHeaders headers, @Context UriInfo info,
                               @Context Application application, @Context Request request,
                               @Context Providers provider) {
         super();
         this.headers = headers;
         this.info = info;
         this.application = application;
         this.request = request;
         this.provider = provider;
      }

      public HolderResolver(@Context HttpHeaders headers, @Context UriInfo info,
                            @Context Application application, @Context Request request) {
         super();
         this.headers = headers;
         this.info = info;
         this.application = application;
         this.request = request;
      }

      public HolderResolver(@Context HttpHeaders headers, @Context UriInfo info,
                            @Context Application application) {
         super();
         this.headers = headers;
         this.info = info;
         this.application = application;
      }

      public HolderResolver(@Context HttpHeaders headers, @Context UriInfo info) {
         super();
         this.headers = headers;
         this.info = info;
      }

      public HolderResolver(@Context HttpHeaders headers) {
         super();
         this.headers = headers;
      }

      @Override
      public HolderClass getContext(Class<?> type) {
         return new HolderClass(headers, info, application, request, provider);
      }
   }

   @Path("resource")
   public static class Resource {

      @Path("contextresolver")
      @GET
      public Response contextresolver(@Context Providers providers) {
         ContextResolver<HolderClass> holder = providers
                 .getContextResolver(HolderClass.class, MediaType.WILDCARD_TYPE);
         return holder.getContext(HolderClass.class).toResponse();
      }
   }




      static Client client;
   @BeforeClass
   public static void setup() throws Exception
   {
      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.getActualProviderClasses().add(HolderResolver.class);
      deployment.getActualResourceClasses().add(Resource.class);
      VertxContainer.start(deployment);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      try
      {
         client.close();
      }
      catch (Exception e)
      {

      }
      VertxContainer.stop();
   }

   @Test
   public void testBasic() throws Exception
   {
      WebTarget target = client.target(generateURL("/resource/contextresolver"));
      String val = target.request().get(String.class);
      Assert.assertEquals("11110", val);
   }
}
