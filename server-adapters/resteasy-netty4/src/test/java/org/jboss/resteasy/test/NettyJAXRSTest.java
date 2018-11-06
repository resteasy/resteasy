package org.jboss.resteasy.test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.SSLContext;
import javax.ws.rs.JAXRS;
import javax.ws.rs.JAXRS.Instance;
import javax.ws.rs.JAXRS.Configuration.SSLClientAuthentication;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.util.SSLCerts;
import org.junit.Assert;
import org.junit.Test;

public class NettyJAXRSTest
{
   @Test
   public void testSSLClientAuthNone() throws Exception
   {
      JAXRS.Configuration configuration = JAXRS.Configuration.builder().host("localhost").port(8443).rootPath("ssl")
            .sslContext(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext())
            .sslClientAuthentication(SSLClientAuthentication.NONE).build();
      CompletionStage<Instance> instance = JAXRS.start(new Application(), configuration);
      instance.toCompletableFuture().get();
      ResteasyClient client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());
      Assert.assertEquals("hello world",
            client.target("https://localhost:8443/ssl/test").request().get(String.class));
   }
   
   public class Application extends javax.ws.rs.core.Application
   {
      public final Set<Object> singletons = new HashSet<Object>();

      public Set<Object> getSingletons()
      {
         singletons.add(new Resource());
         return singletons;
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
