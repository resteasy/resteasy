package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.KeyStoreKeyRepository;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.GenericType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest
{
   public static KeyPair keys;
   public static DosetaKeyRepository repository;

   @Path("/signed")
   public static interface SigningProxy
   {
      @GET
      @Verify
      @Produces("text/plain")
      public String hello();

      @POST
      @Consumes("text/plain")
      @Signed(selector = "test", domain="samplezone.org")
      public void postSimple(String input);
   }

   @BeforeClass
   public static void setup() throws Exception
   {
      repository = new DosetaKeyRepository();
      repository.setKeyStorePath("test.jks");
      repository.setKeyStorePassword("password");
      repository.setUseDns(false);
      repository.start();
   }

   @Test
   public void testVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);

      System.out.println(response.getHeaders().getFirst(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      System.out.println(response.getEntity());
   }

   @Test
   public void testFailedVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/bad-signature");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);

      System.out.println(response.getHeaders().getFirst(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      try
      {
         System.out.println(response.getEntity());
         throw new RuntimeException("UNREACHABLE!!!");
      }
      catch (UnauthorizedSignatureException e)
      {
         System.out.println("We expect this failure: " + e.getMessage());
      }

   }


   @Test
   public void testPost() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("test");
      contentSignature.setDomain("samplezone.org");
      request.getAttributes().put(KeyRepository.class.getName(), repository);

      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testExpiresMinutes() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-minute");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);

      System.out.println(response.getHeaders().getFirst(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      System.out.println(response.getEntity());
   }

   /**
    * Test that expiration works
    * 
    * @throws Exception
    */
   @Test
   public void testExpiresFail() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-short");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.getEntity();
         throw new Exception("unreachable!");
      }
      catch (UnauthorizedSignatureException e)
      {
         System.out.println("Verification failed: " + e.getMessage());
      }


   }

   @Test
   public void testProxy() throws Exception
   {
      Map<String, Object> attributes = new HashMap<String, Object>();
      attributes.put(KeyRepository.class.getName(), repository);
      SigningProxy proxy = ProxyFactory.create(SigningProxy.class, "http://localhost:9095", attributes);
      String output = proxy.hello();
      proxy.postSimple("hello world");
   }

}
