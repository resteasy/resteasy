package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.signature.Signed;
import org.jboss.resteasy.annotations.security.signature.Verify;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.security.keys.KeyStoreKeyRepository;
import org.jboss.resteasy.security.signing.ContentSignature;
import org.jboss.resteasy.security.signing.ContentSignatures;
import org.jboss.resteasy.security.signing.UnauthorizedSignatureException;
import org.jboss.resteasy.security.signing.Verification;
import org.jboss.resteasy.security.signing.Verifier;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.GenericType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest
{
   public static KeyPair keys;
   public static KeyRepository repository;

   @Path("/signed")
   public static interface SigningProxy
   {
      @GET
      @Verify(keyAlias = "test")
      @Produces("text/plain")
      public String hello();

      @POST
      @Consumes("text/plain")
      @Signed(keyAlias = "test")
      public void postSimple(String input);
   }

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
   public void testManual() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/manual");
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);
   }

   @Test
   public void testSigningUseKey() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed");
      ClientResponse<?> response = request.get();
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      System.out.println("Response entity: " + entity.getEntity());

      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);
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
   public void testSigningWithSigner() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/with-signer");
      ClientResponse<?> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);
   }

   @Test
   public void testTimestamp() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/stamped");
      ClientResponse<?> response = request.get(String.class);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());

      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);

      // stale check.  Timestamp can be older than 100 seconds

      Assert.assertFalse(contentSignature.isStale(100, 0, 0, 0, 0, 0));

   }

   @Test
   public void testStaleTimestamp() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/stamped");
      ClientResponse<?> response = request.get(String.class);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());

      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);

      // stale check.  Timestamp can be older than 100 seconds
      Thread.sleep(1500);  // sleep so that its stale

      Assert.assertTrue(contentSignature.isStale(1, 0, 0, 0, 0, 0));

   }

   @Test
   public void testExpiresHour() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-minute");
      ClientResponse<?> response = request.get(String.class);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());

      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);

      // make sure its not expired

      Assert.assertFalse(contentSignature.isExpired());

   }

   @Test
   public void testExpiresFail() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/expires-short");
      ClientResponse<?> response = request.get(String.class);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());

      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>(){});
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);

      // make sure its not expired
      Thread.sleep(1500);
      Assert.assertTrue(contentSignature.isExpired());

   }

   @Test
   public void testManualVerification() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/verify-manual");
      ContentSignature contentSignature = new ContentSignature();
      contentSignature.setAttribute("code", "hello", true, true);
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testVerifyBySigner() throws Exception
   {
      ClientRequest request = new ClientRequest("http://localhost:9095/signed/by-signer");
      ContentSignature contentSignature = new ContentSignature();
      contentSignature.setSigner("test", true, true);
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testProxy() throws Exception
   {
      Map<String, Object> attributes = new HashMap<String, Object>();
      attributes.put(KeyRepository.class.getName(), repository);
      SigningProxy proxy = ProxyFactory.create(SigningProxy.class, "http://localhost:8080", attributes);
      String output = proxy.hello();
      proxy.postSimple("hello");
   }

}
