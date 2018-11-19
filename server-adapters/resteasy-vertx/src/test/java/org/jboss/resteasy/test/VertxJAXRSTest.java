package org.jboss.resteasy.test;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Configuration.Builder;
import javax.ws.rs.JAXRS.Instance;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;

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
               Assert.assertEquals("hello world", client.target("http://localhost:8080/contextPath/base/test")
                     .request().get(String.class));
            }
         });
         request.toCompletableFuture().get();
      }
      catch (Throwable e)
      {
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

   @Test
   public void testSSL() throws Exception
   {
      Builder builder = JAXRS.Configuration.builder().host("localhost").port(8443).rootPath("ssl");
      HttpServerOptions options = new HttpServerOptions().setSsl(true).setKeyStoreOptions(
            new JksOptions().setPath("sni/default_server_keystore.jks").setPassword("secret"));
      builder.property(HttpServerOptions.class.getName(), options);
      CompletionStage<Instance> instance = JAXRS.start(new MyApp(), builder.build());

      ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilderImpl();

      KeyStore ks = KeyStore.getInstance("JKS");
      loadKeyStore(ks, fullPath("sni/default_server_keystore.jks"), "secret".toCharArray());
      resteasyClientBuilder.trustStore(ks);

      try
      {
         CompletionStage<Void> request = instance.thenAccept(ins -> {
            try (Client client = resteasyClientBuilder.build())
            {
               Assert.assertEquals("hello world",
                     client.target("https://localhost:8443/ssl/base/test").request().get(String.class));
            }
         });
         request.toCompletableFuture().get();
      }
      catch (Throwable e)
      {
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

   private static void loadKeyStore(KeyStore ks, String keyStoreFileName, char[] keyStorePassword) throws IOException,
         GeneralSecurityException
   {

      try (InputStream is = new BufferedInputStream(new FileInputStream(keyStoreFileName)))
      {
         ks.load(is, keyStorePassword);
      }
   }

   private String fullPath(String path)
   {
      if (path == null)
      {
         return null;
      }
      return VertxJAXRSTest.class.getClassLoader().getResource(path).getPath();
   }

}
