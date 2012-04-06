package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import se.unlogic.eagledns.EagleDNS;

import java.security.KeyPair;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest
{
   public static KeyPair keys;
   public static DosetaKeyRepository repository;

   @BeforeClass
   public static void setup() throws Exception
   {
      repository = new DosetaKeyRepository();
      repository.setKeyStorePath("client.jks");
      repository.setKeyStorePassword("password");

      // Use DNS to discover public keys!!!
      repository.setUseDns(true);
      // Point to our TEST DNS server
      repository.setDnsUri("dns://localhost:6363");
      repository.start();

      configureDNS();
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
   }


   @Test
   public void testBasicVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);

      System.out.println("Client: ");
      System.out.println(response.getHeaders().getFirst(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // verification doesn't happen unless you try and extract the entity
      System.out.println(response.getEntity());
   }

   @Test
   public void testPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("bill");
      contentSignature.setDomain("client.com");
      request.getAttributes().put(KeyRepository.class.getName(), repository);

      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }
}
