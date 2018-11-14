package org.jboss.resteasy.test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Instance;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.Assert;
import org.junit.Test;

public class VertxJAXRSTest
{
   @Path("/test")
   public static class Resource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }
   }

   @ApplicationPath("/base")
   public static class MyApp extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(Resource.class);
         return classes;
      }
   }

   @Test
   public void testJAXRS() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8080)
            .rootPath("contextPath").build();
      CompletionStage<Instance> instance = JAXRS.start(new MyApp(), configuration);
      try
      {
         CompletionStage<Void> request = instance.thenAccept(ins -> {
            try (Client client = ClientBuilder.newClient())
            {
               Assert.assertEquals("hello world", client.target("http://localhost:8080/contextPath/base/test").request()
                     .get(String.class));
            }
         });
         request.toCompletableFuture().get();
      }catch(Throwable e) {
         e.printStackTrace();
      }
      finally
      {
         if (instance.toCompletableFuture().isCompletedExceptionally())
         {
            Assert.fail("Failed to start server with bootstrap api");
         }
         else
         {
            instance.toCompletableFuture().get().stop();
         }
      }
   }

   private ResteasyClient createClientWithCertificate(SSLContext sslContext, String... sniName)
   {
      ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilderImpl();
      if (sslContext != null)
      {
         resteasyClientBuilder.sslContext(sslContext);
      }
      if (sniName != null)
      {
         resteasyClientBuilder.sniHostNames(sniName);
      }
      return resteasyClientBuilder.build();
   }
}
