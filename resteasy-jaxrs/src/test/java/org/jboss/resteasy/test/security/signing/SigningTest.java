package org.jboss.resteasy.test.security.signing;

import org.jboss.resteasy.annotations.security.signature.After;
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
import org.jboss.resteasy.test.BaseResourceTest;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SigningTest extends BaseResourceTest
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


      dispatcher.getDefaultContextObjects().put(KeyRepository.class, repository);
      /*
      deployment.getProviderFactory().registerProvider(DigitalSigningInterceptor.class);
      deployment.getProviderFactory().registerProvider(DigitalSigningHeaderDecorator.class);
      deployment.getProviderFactory().registerProvider(DigitalVerificationInterceptor.class);
      deployment.getProviderFactory().registerProvider(DigitalVerificationHeaderDecorator.class);
      */
      addPerRequestResource(SignedResource.class);
   }

   @Path("/signed")
   public static interface SigningProxy
   {
      @GET
      @Verify(keyAlias = "test")
      @Produces("text/plain")
      @Path("bad-signature")
      public String bad();

      @GET
      @Verify(keyAlias = "test")
      @Produces("text/plain")
      public String hello();

      @POST
      @Consumes("text/plain")
      @Signed(keyAlias = "test")
      public void postSimple(String input);
   }


   @Path("/signed")
   public static class SignedResource
   {
      @GET
      @Produces("text/plain")
      @Path("bad-signature")
      public Response badSignature()
      {
         ContentSignature signature = new ContentSignature();
         signature.setHexSignature("0f03");
         return Response.ok("hello world").header("Content-Signature", signature).build();
      }

      @GET
      @Produces("text/plain")
      @Path("manual")
      public Response getManual()
      {
         ContentSignature signature = new ContentSignature();
         signature.setKeyAlias("test");
         Response.ResponseBuilder builder = Response.ok("hello");
         builder.header("Content-Signature", signature);
         return builder.build();
      }

      @GET
      @Signed(keyAlias = "test")
      @Produces("text/plain")
      public String hello()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test")
      @Produces("text/plain")
      @Path("with-signer")
      public String withSigner()
      {
         return "hello world";
      }

      @POST
      @Consumes("text/plain")
      @Verify(keyAlias = "test")
      public void post(@HeaderParam("Content-Signature") ContentSignatures signatures, String input)
      {
         Assert.assertNotNull(signatures);
         Assert.assertEquals(1, signatures.getSignatures().size());
         Assert.assertEquals(input, "hello world");
      }

      @POST
      @Consumes("text/plain")
      @Path("by-signer")
      @Verify
      public void postBySigner(@HeaderParam("Content-Signature") ContentSignatures signatures, String input)
      {
         Assert.assertNotNull(signatures);
         Assert.assertEquals(1, signatures.getSignatures().size());
         Assert.assertEquals(input, "hello world");
      }

      @POST
      @Consumes("text/plain")
      @Path("verify-manual")
      public void verifyManual(@HeaderParam("Content-Signature") ContentSignature signature, @Context HttpHeaders headers, MarshalledEntity<String> input) throws Exception
      {
         Assert.assertNotNull(signature);
         Assert.assertEquals(input.getEntity(), "hello world");

         Assert.assertTrue(signature.verify(headers.getRequestHeaders(), input.getMarshalledBytes(), keys.getPublic()));
      }

      @GET
      @Signed(signer = "test", timestamped = true)
      @Produces("text/plain")
      @Path("stamped")
      public String getStamp()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(seconds = 1))
      @Produces("text/plain")
      @Path("expires-short")
      public String getExpiresShort()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(minutes = 1))
      @Produces("text/plain")
      @Path("expires-minute")
      public String getExpiresMinute()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(hours = 1))
      @Produces("text/plain")
      @Path("expires-hour")
      public String getExpiresHour()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(days = 1))
      @Produces("text/plain")
      @Path("expires-day")
      public String getExpiresDay()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(months = 1))
      @Produces("text/plain")
      @Path("expires-month")
      public String getExpiresMonth()
      {
         return "hello world";
      }

      @GET
      @Signed(signer = "test", expires = @After(years = 1))
      @Produces("text/plain")
      @Path("expires-year")
      public String getExpiresYear()
      {
         return "hello world";
      }
   }

   @Test
   public void testSigningUseKey() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
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
   public void testSigningManual() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      ClientResponse<MarshalledEntity<String>> response = request.get(new GenericType<MarshalledEntity<String>>()
      {
      });
      Assert.assertEquals(200, response.getStatus());
      MarshalledEntity<String> marshalledEntity = response.getEntity();
      Assert.assertEquals("hello world", marshalledEntity.getEntity());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      System.out.println("Content-Signature:  " + signatureHeader);
      Assert.assertNotNull(signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);
      boolean verified = contentSignature.verify(response.getHeaders(), marshalledEntity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);
   }

   @Test
   public void testSigningWithSigner() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/with-signer"));
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
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      ContentSignatures signatures = new ContentSignatures();
      ContentSignature contentSignature = signatures.addNew();
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", signatures);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBySignerVerification() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/by-signer"));
      ContentSignatures signatures = new ContentSignatures();
      ContentSignature contentSignature = signatures.addNew();
      contentSignature.setSigner("test", true, true);
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", signatures);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testManualVerification() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/verify-manual"));
      ContentSignatures signatures = new ContentSignatures();
      ContentSignature contentSignature = signatures.addNew();
      contentSignature.setAttribute("code", "hello", true, true);
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header("Content-Signature", signatures);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBasicVerificationRepository() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      ContentSignatures signatures = new ContentSignatures();
      ContentSignature contentSignature = signatures.addNew();
      contentSignature.setKeyAlias("test");
      request.getAttributes().put(KeyRepository.class.getName(), repository);

      request.header("Content-Signature", signatures);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBasicVerificationBadSignature() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      request.header("Content-Signature", "signature=0f");
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(401, response.getStatus());
   }

   @Test
   public void testBasicVerificationNoSignature() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(401, response.getStatus());
   }

   @Test
   public void testTimestampSignature() throws Exception
   {
      ContentSignature signature = new ContentSignature();
      signature.setTimestamp();
      signature.sign(new HashMap(), "hello world".getBytes(), null, keys.getPrivate());
      String sig = signature.toString();
      System.out.println("Content-Signature: " + sig);
      signature = new ContentSignature(sig);

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

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/stamped"));
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

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/stamped"));
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
         UnauthorizedSignatureException signatureException = (UnauthorizedSignatureException) e.getCause();
         Assert.assertEquals("Signature is stale", signatureException.getResults().getFirstResult(verification).getFailureReason());
      }


   }

   @Test
   public void testExpiresHour() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-hour"));
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

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-minute"));
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

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-day"));
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
   }

   @Test
   public void testExpiresMonths() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-month"));
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
   }

   @Test
   public void testExpiresYears() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-year"));
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

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/expires-short"));
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
         UnauthorizedSignatureException signatureException = (UnauthorizedSignatureException) e.getCause();
         Assert.assertEquals("Signature expired", signatureException.getResults().getFirstResult(verification).getFailureReason());
      }


   }

   @Test
   public void testManualFail() throws Exception
   {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      KeyPair keyPair = kpg.genKeyPair();

      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setKey(keyPair.getPublic());

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/manual"));
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertNotNull(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      try
      {
         String output = response.getEntity();
      }
      catch (ReaderException e)
      {
         Assert.assertTrue(e.getCause() instanceof UnauthorizedSignatureException);
         UnauthorizedSignatureException signatureException = (UnauthorizedSignatureException) e.getCause();
         Assert.assertEquals("Signature verification failed", signatureException.getResults().getFirstResult(verification).getFailureReason());
      }


   }

   @Test
   public void testManual() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);
      verification.setKeyAlias("test");

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/manual"));
      ClientResponse<String> response = request.get(String.class);
      response.getAttributes().put(Verifier.class.getName(), verifier);
      System.out.println(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertNotNull(response.getHeaders().getFirst("Content-Signature"));
      Assert.assertEquals(200, response.getStatus());
      String output = response.getEntity();
      Assert.assertEquals("hello", output);
   }

   @Test
   public void testBadSignature() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/bad-signature"));
      ClientResponse<?> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(ContentSignature.CONTENT_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Content-Signature:  " + signatureHeader);

      ContentSignature contentSignature = new ContentSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>()
      {
      });
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertFalse(verified);
   }

   @Test
   public void testProxy() throws Exception
   {
      Map<String, Object> attributes = new HashMap<String, Object>();
      attributes.put(KeyRepository.class.getName(), repository);
      SigningProxy proxy = ProxyFactory.create(SigningProxy.class, TestPortProvider.generateURL(""), attributes);
      String output = proxy.hello();
      proxy.postSimple("hello world");
   }


   @Test
   public void testBadSignatureProxy() throws Exception
   {
      Map<String, Object> attributes = new HashMap<String, Object>();
      attributes.put(KeyRepository.class.getName(), repository);
      SigningProxy proxy = ProxyFactory.create(SigningProxy.class, TestPortProvider.generateURL(""), attributes);
      try
      {
         String output = proxy.bad();
      }
      catch (ReaderException e)
      {
         Assert.assertTrue(e.getCause() instanceof UnauthorizedSignatureException);
      }
   }

}
