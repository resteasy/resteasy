package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.security.keys.KeyStoreKeyRepository;
import org.jboss.resteasy.security.signing.ContentSignature;
import org.jboss.resteasy.security.signing.ContentSignatures;
import org.jboss.resteasy.security.signing.UnauthorizedSignatureException;
import org.jboss.resteasy.security.signing.Verification;
import org.jboss.resteasy.security.signing.Verifier;
import org.jboss.resteasy.spi.ReaderException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest
{
   public static KeyPair keys;
   public static KeyRepository repository;

   @BeforeClass
   public static void setup() throws Exception
   {
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.jks");
      if (is != null) System.out.println("input is not null");
      repository = new KeyStoreKeyRepository(is, "password");
      PrivateKey privateKey = repository.getPrivateKey("test");
      if (privateKey == null) throw new Exception("Private Key is null!!!");
      else System.out.println("PrivateKey was not null!!");
      PublicKey publicKey = repository.getPublicKey("test");
      keys = new KeyPair(publicKey, privateKey);
   }

   @Test
   public void testSigningUseKey() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      System.out.println("Content-Signature:  " + signatureHeader);
      Assert.assertNotNull(signatureHeader);
      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      Signature verifier = Signature.getInstance("SHA256withRSA");
      verifier.initVerify(keys.getPublic());
      verifier.update(response.getEntity().getBytes());
      Assert.assertTrue(verifier.verify(contentSignature.getSignature()));
   }

   @Test
   public void testSigningWithSigner() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/with-signer");
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      System.out.println("Content-Signature:  " + signatureHeader);
      Assert.assertNotNull(signatureHeader);
      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      Signature verifier = Signature.getInstance("SHA256withRSA");
      verifier.initVerify(keys.getPublic());
      verifier.update("test".getBytes());
      verifier.update(response.getEntity().getBytes());
      Assert.assertTrue(verifier.verify(contentSignature.getSignature()));
   }

   @Test
   public void testBasicVerification() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      ContentSignatures signatures = new ContentSignatures();
      ContentSignature contentSignature = signatures.addNew();
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", signatures);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBasicVerificationBadSignature() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      request.header("Content-Signature", "signature=0f");
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(401, response.getStatus());
   }

   @Test
   public void testTimestamp() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setStaleCheck(true);
      verification.setStaleSeconds(100);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/stamped");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();


   }

   @Test
   public void testStaleTimestamp() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setStaleCheck(true);
      verification.setStaleSeconds(1);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/stamped");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.getEntity();
      }
      catch (ReaderException e)
      {
         Assert.assertTrue(e.getCause() instanceof UnauthorizedSignatureException);
      }


   }

   @Test
   public void testExpiresHour() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-hour");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
   }

   @Test
   public void testExpiresMinutes() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-minute");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
   }

   @Test
   public void testExpiresDays() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-day");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
   }


   @Test
   public void testExpiresFail() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-short");
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.getEntity();
      }
      catch (ReaderException e)
      {
         Assert.assertTrue(e.getCause() instanceof UnauthorizedSignatureException);
      }


   }
}
