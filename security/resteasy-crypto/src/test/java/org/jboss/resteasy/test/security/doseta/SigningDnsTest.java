package org.jboss.resteasy.test.security.doseta;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import se.unlogic.eagledns.EagleDNS;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningDnsTest
{
   private static NettyJaxrsServer server;
   private static ResteasyDeployment deployment;
   public static DosetaKeyRepository clientRepository;
   public static DosetaKeyRepository serverRepository;
   public static PrivateKey badKey;
   private static Client client;

   public Registry getRegistry()
   {
      return deployment.getRegistry();
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return deployment.getProviderFactory();
   }

   /**
    * @param resource
    */
   public static void addPerRequestResource(Class<?> resource)
   {
      deployment.getRegistry().addPerRequestResource(resource);
   }   
   
   @BeforeClass
   public static void setup() throws Exception
   {
      server = new NettyJaxrsServer();
      server.setPort(TestPortProvider.getPort());
      server.setRootResourcePath("/");
      server.start();
      deployment = server.getDeployment();
      
      clientRepository = new DosetaKeyRepository();
      clientRepository.setKeyStorePath("test1.jks");
      clientRepository.setKeyStorePassword("password");
      clientRepository.setUseDns(true);
      clientRepository.setDnsUri("dns://localhost:6363");
      clientRepository.start();

      serverRepository = new DosetaKeyRepository();
      serverRepository.setKeyStorePath("test2.jks");
      serverRepository.setKeyStorePassword("password");
      serverRepository.setUseDns(true);
      serverRepository.setDnsUri("dns://localhost:6363");
      serverRepository.start();

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      badKey = keyPair.getPrivate();


      deployment.getDispatcher().getDefaultContextObjects().put(KeyRepository.class, serverRepository);
      addPerRequestResource(SignedResource.class);
      configureDNS();
      client = ClientBuilder.newClient();
   }

   private static EagleDNS dns;

   public static void configureDNS() throws Exception
   {
      dns = new EagleDNS();
      dns.setConfigClassPath("dns/conf/config.xml");
      dns.start();
   }

   @AfterClass
   public static void shutdownDns()
   {
      dns.shutdown();
      client.close();
      server.stop();
      server = null;
      deployment = null;
   }


   @Path("/signed")
   public static class SignedResource
   {
      @GET
      @Produces("text/plain")
      @Path("bad-signature")
      public Response badSignature() throws Exception
      {
         DKIMSignature signature = new DKIMSignature();
         signature.setDomain("samplezone.org");
         signature.setSelector("test2");
         signature.setPrivateKey(badKey);

         return Response.ok("hello world").header(DKIMSignature.DKIM_SIGNATURE, signature).build();
      }

      @GET
      @Signed(selector = "test2", domain = "samplezone.org")
      @Produces("text/plain")
      public String hello()
      {
         return "hello world";
      }

      @POST
      @Consumes("text/plain")
      @Verify
      public void post(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature, String input)
      {
         Assert.assertNotNull(signature);
         Assert.assertEquals(input, "hello world");
//         System.out.println(signature);
      }

   }

   @Test
   public void testBasicVerificationRepository() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/signed"));
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("test1");
      contentSignature.setDomain("samplezone.org");
      target.property(KeyRepository.class.getName(), clientRepository);
      Builder request = target.request();
      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      Response response = request.post(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(204, response.getStatus());
      response.close();

   }

   @Test
   public void testBasicVerificationBadSignature() throws Exception
   {
      Builder request = client.target(TestPortProvider.generateURL("/signed")).request();
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("test1");
      contentSignature.setDomain("samplezone.org");
      contentSignature.setPrivateKey(badKey);
      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      Response response = request.post(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(401, response.getStatus());
      response.close();
   }

   @Test
   public void testBasicVerificationNoSignature() throws Exception
   {
      Builder request = client.target(TestPortProvider.generateURL("/signed")).request();
      Response response = request.post(Entity.entity("hello world", "text/plain"));
      Assert.assertEquals(401, response.getStatus());
      response.close();
   }

}
