package org.jboss.resteasy.test;

//import org.jboss.resteasy.client.jaxrs.ResteasyClient;
//import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
//import org.jboss.resteasy.test.TestPortProvider;
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import javax.net.ssl.SSLContext;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.ProcessingException;
//import javax.ws.rs.Produces;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;

/**
 * Tests SNI capabilities for Netty based JaxRS server.
 *
 * @author Sebastian Łaskawiec
 * @see https://issues.jboss.org/browse/RESTEASY-1431
 *
 * TODO what is SNI??
public class SniTest
{

   private static ReactorNettyJaxrsServer server;

   @BeforeClass
   public static void setup()
   {
      SniConfiguration sniConfiguration = new SniConfiguration(SSLCerts.DEFAULT_SERVER_KEYSTORE.getSslContext());
      sniConfiguration.addSniMapping("sni", SSLCerts.SNI_SERVER_KEYSTORE.getSslContext());
      sniConfiguration.addSniMapping("untrusted", SSLCerts.NO_TRUSTED_CLIENTS_KEYSTORE.getSslContext());

      server = new ReactorNettyJaxrsServer();
      server.setSniConfiguration(sniConfiguration);
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("");
      server.setSecurityDomain(null);
      server.getDeployment().getScannedResourceClasses().add(ResteasyTrailingSlashTest.Resource.class.getName());
      server.start();
   }

   @AfterClass
   public static void stop() throws Exception {
      server.stop();
   }

   @Test
   public void testTrustedClient()
   {
      //given
      Client client = createClientWithCertificate(SSLCerts.DEFAULT_TRUSTSTORE.getSslContext());

      //when
      String returnValue = callRestService(client);

      //then
      Assert.assertNotNull(returnValue);
   }

   @Test(expected = ProcessingException.class)
   public void testTrustedClientButWithNoSNI()
   {
      //given
      Client client = createClientWithCertificate(SSLCerts.SNI_SERVER_KEYSTORE.getSslContext());

      //when
      callRestService(client);
   }

   @Test
   public void testSniClient()
   {
      //given
      ResteasyClient client = createClientWithCertificate(SSLCerts.SNI_TRUSTSTORE.getSslContext(), "sni");

      //when
      String returnValue = callRestService(client);

      //then
      Assert.assertNotNull(returnValue);
   }

   private ResteasyClient createClientWithCertificate(SSLContext sslContext, String... sniName)
   {
      ResteasyClientBuilder resteasyClientBuilder = (ResteasyClientBuilder)ClientBuilder.newBuilder();
      if(sslContext != null) {
         resteasyClientBuilder.sslContext(sslContext);
      }
      if(sniName != null) {
         resteasyClientBuilder.sniHostNames(sniName);
      }
      return resteasyClientBuilder.build();
   }

   private String callRestService(Client client) {
      WebTarget target = client.target(String.format("https://%s:%d/test", TestPortProvider.getHost(), TestPortProvider.getPort()));
      return target.request().get(String.class);
   }

   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/test/")
      @Produces(MediaType.TEXT_PLAIN)
      public String get()
      {
         return "hello world";
      }
   }

}
*/