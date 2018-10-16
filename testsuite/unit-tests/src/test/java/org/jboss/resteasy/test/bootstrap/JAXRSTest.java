package org.jboss.resteasy.test.bootstrap;

import java.util.HashSet;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.ws.rs.GET;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Configuration.SSLClientAuthentication;
import javax.ws.rs.JAXRS.Instance;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.junit.Assert;
import org.junit.Test;

public class JAXRSTest
{

   @Test
   public void testJAXRS() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8080)
            .rootPath("bootstrap").build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), configuration);
      try
      {
         CompletionStage<Void> request = instance.thenAccept(ins -> {
            try (Client client = ClientBuilder.newClient())
            {
               Assert.assertEquals("BootStrapApi", client.target("http://localhost:8080/bootstrap/produces/string")
                     .request().get(String.class));
            }
         });
         request.toCompletableFuture().get();
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
   public void testFailedStartJAXRS() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8081).rootPath("error")
            .build();
      CompletionStage<Instance> instance = JAXRS.start(new ErrorApplication(), configuration);
      try
      {
         instance.toCompletableFuture().get();
      }
      catch (Exception e)
      {
         Assert.assertTrue("Server failed is expected", e.getMessage().indexOf("Could not find constructor") > -1);
      }

   }

   @Test
   public void testSSLClientAuthNone() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8443).rootPath("ssl")
            .sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
            .sslClientAuthentication(SSLClientAuthentication.NONE).build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), configuration);
      instance.toCompletableFuture().get();

      ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
      Assert.assertEquals("BootStrapApi",
            client.target("https://localhost:8443/ssl/produces/string").request().get(String.class));
   }

   @Test
   public void testSSLClientAuthRequired() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8444)
            .rootPath("needclientauth").sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
            .sslClientAuthentication(SSLClientAuthentication.MANDATORY).build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), configuration);
      instance.toCompletableFuture().get();

      ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
      try
      {
         Assert.assertEquals("BootStrapApi", client.target("https://localhost:8444/needclientauth/produces/string")
               .request().get(String.class));
      }
      catch (Throwable e)
      {
         Assert.assertTrue(e.getCause() instanceof SSLHandshakeException);
      }
   }

   @Test
   public void testSSLClientAuthWant() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8445)
            .rootPath("wantclientauth").sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
            .sslClientAuthentication(SSLClientAuthentication.OPTIONAL).build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), configuration);
      instance.toCompletableFuture().get();

      ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
      Assert.assertEquals("BootStrapApi", client.target("https://localhost:8445/wantclientauth/produces/string")
            .request().get(String.class));
   }
   
   @Test
   public void testPropertyProvider() throws Exception {
      TestPropertyProvider propertyProvider = new TestPropertyProvider();
      JAXRS.Configuration config = JAXRS.Configuration.builder().from((name, type) -> {
         return propertyProvider.getValue(name, type);
      }).build();
      CompletionStage<Instance> instance = JAXRS.start(new StandaloneApplication(), config);
      instance.toCompletableFuture().get();
      try (Client client = ClientBuilder.newClient())
      {
         Assert.assertEquals("BootStrapApi", client.target("http://localhost:8999/propertyProvider/produces/string")
               .request().get(String.class));
      }
   }

   class TestPropertyProvider {
      public Optional<Object> getValue(String name, Class<?> type) {
          if (name.equals(JAXRS.Configuration.HOST))
             return Optional.of("localhost");
          if (name.equals(JAXRS.Configuration.PORT)) {
             return Optional.of(8999);
          }
          if (name.equals(JAXRS.Configuration.ROOT_PATH)) {
             return Optional.of("propertyProvider");
          }
          return Optional.empty();
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

   public class StandaloneApplication extends javax.ws.rs.core.Application
   {
      public final Set<Object> singletons = new HashSet<Object>();

      public Set<Object> getSingletons()
      {
         singletons.add(new StringResource());
         return singletons;
      }

   }

   public class ErrorApplication extends javax.ws.rs.core.Application
   {
      // Error applicaton the StringResource requires a constructor
      public final Set<Class<?>> classes = new HashSet<Class<?>>();

      public Set<Class<?>> getClasses()
      {
         classes.add(StringResource.class);
         return classes;
      }
   }

   @Path("/")
   public class StringResource
   {
      @GET
      @Path("produces/string")
      @Produces("text/plain")
      public String produceString()
      {
         return "BootStrapApi";
      }
   }
}
