package org.jboss.resteasy.tests.signature;

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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.security.KeyPair;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest
{
   public static KeyPair keys;
   public static DosetaKeyRepository repository;
   public static Client client;

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
   }


   @Test
   public void testBasicVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      WebTarget target = client.target("http://localhost:9095/signed");
      Invocation.Builder request = target.request();
      request.property(Verifier.class.getName(), verifier);
      Response response = request.get();

      System.out.println("Client: ");
      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // verification doesn't happen unless you try and extract the entity
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testPost() throws Exception
   {
      WebTarget target = client.target("http://localhost:9095/signed");
      Invocation.Builder request = target.request();
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("bill");
      contentSignature.setDomain("client.com");
      request.property(KeyRepository.class.getName(), repository);

      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      Response response = request.post(Entity.text("hello world"));
      Assert.assertEquals(204, response.getStatus());
      response.close();


   }
}
