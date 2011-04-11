package org.jboss.resteasy.test.security.doseta;

import org.jboss.resteasy.annotations.security.doseta.After;
import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.security.doseta.DosetaSignature;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.jboss.resteasy.security.keys.KeyRepository;
import org.jboss.resteasy.security.keys.KeyStoreKeyRepository;
import org.jboss.resteasy.spi.MarshalledEntity;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.Base64;
import org.jboss.resteasy.util.GenericType;
import org.jboss.resteasy.util.ParameterParser;
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
   public static PrivateKey badKey;


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

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      badKey = keyPair.getPrivate();


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
      public Response badSignature() throws Exception
      {
         DosetaSignature signature = new DosetaSignature();
         signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());

         byte[] sig = {0x0f, 0x03};
         String encodedBadSig = Base64.encodeBytes(sig);

         ParameterParser parser = new ParameterParser();
         String s = signature.toString();
         String header = parser.setAttribute(s.toCharArray(), 0, s.length(), ';', "b", encodedBadSig);

         signature.setSignature(sig);
         return Response.ok("hello world").header(DosetaSignature.DOSETA_SIGNATURE, header).build();
      }

      @GET
      @Produces("text/plain")
      @Path("bad-hash")
      public Response badHash() throws Exception
      {
         DosetaSignature signature = new DosetaSignature();
         signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());

         return Response.ok("hello").header(DosetaSignature.DOSETA_SIGNATURE, signature.toString()).build();
      }

      @GET
      @Produces("text/plain")
      @Path("manual")
      public Response getManual()
      {
         DosetaSignature signature = new DosetaSignature();
         signature.setKeyAlias("test");
         Response.ResponseBuilder builder = Response.ok("hello");
         builder.header(DosetaSignature.DOSETA_SIGNATURE, signature);
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
      @Signed(domain = "test")
      @Produces("text/plain")
      @Path("with-signer")
      public String withSigner()
      {
         return "hello world";
      }

      @POST
      @Consumes("text/plain")
      @Verify(keyAlias = "test")
      public void post(@HeaderParam("Doseta-Signature") DosetaSignature signature, String input)
      {
         Assert.assertNotNull(signature);
         Assert.assertEquals(input, "hello world");
      }

      @POST
      @Consumes("text/plain")
      @Path("by-domain")
      @Verify
      public void postByDomain(@HeaderParam("Doseta-Signature") DosetaSignature signature, String input)
      {
         Assert.assertNotNull(signature);
         Assert.assertEquals(input, "hello world");
      }

      @POST
      @Consumes("text/plain")
      @Path("verify-manual")
      public void verifyManual(@HeaderParam("Doseta-Signature") DosetaSignature signature, @Context HttpHeaders headers, MarshalledEntity<String> input) throws Exception
      {
         Assert.assertNotNull(signature);
         Assert.assertEquals(input.getEntity(), "hello world");

         Assert.assertTrue(signature.verify(headers.getRequestHeaders(), input.getMarshalledBytes(), keys.getPublic()));
      }

      @GET
      @Signed(domain = "test", timestamped = true)
      @Produces("text/plain")
      @Path("stamped")
      public String getStamp()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(seconds = 1))
      @Produces("text/plain")
      @Path("expires-short")
      public String getExpiresShort()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(minutes = 1))
      @Produces("text/plain")
      @Path("expires-minute")
      public String getExpiresMinute()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(hours = 1))
      @Produces("text/plain")
      @Path("expires-hour")
      public String getExpiresHour()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(days = 1))
      @Produces("text/plain")
      @Path("expires-day")
      public String getExpiresDay()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(months = 1))
      @Produces("text/plain")
      @Path("expires-month")
      public String getExpiresMonth()
      {
         return "hello world";
      }

      @GET
      @Signed(domain = "test", expires = @After(years = 1))
      @Produces("text/plain")
      @Path("expires-year")
      public String getExpiresYear()
      {
         return "hello world";
      }
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
      String signatureHeader = response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE);
      System.out.println("Doseta-Signature:  " + signatureHeader);

      for (String name : response.getHeaders().keySet())
      {
         System.out.println("Header: " + name);
      }
      Assert.assertNotNull(signatureHeader);

      DosetaSignature contentSignature = new DosetaSignature(signatureHeader);
      boolean verified = contentSignature.verify(response.getHeaders(), marshalledEntity.getMarshalledBytes(), keys.getPublic());
      Assert.assertTrue(verified);
   }

   @Test
   public void testBasicVerification() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      DosetaSignature contentSignature = new DosetaSignature();
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header(DosetaSignature.DOSETA_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testByDomainVerification() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/by-domain"));
      DosetaSignature contentSignature = new DosetaSignature();
      contentSignature.setDomainIdentity("test");
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header(DosetaSignature.DOSETA_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testManualVerification() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/verify-manual"));
      DosetaSignature contentSignature = new DosetaSignature();
      contentSignature.setAttribute("code", "hello");
      contentSignature.setPrivateKey(keys.getPrivate());
      request.header(DosetaSignature.DOSETA_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBasicVerificationRepository() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      DosetaSignature contentSignature = new DosetaSignature();
      contentSignature.setKeyAlias("test");
      request.getAttributes().put(KeyRepository.class.getName(), repository);

      request.header(DosetaSignature.DOSETA_SIGNATURE, contentSignature);
      request.body("text/plain", "hello world");
      ClientResponse response = request.post();
      Assert.assertEquals(204, response.getStatus());


   }

   @Test
   public void testBasicVerificationBadSignature() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed"));
      DosetaSignature contentSignature = new DosetaSignature();
      contentSignature.setPrivateKey(badKey);
      request.header(DosetaSignature.DOSETA_SIGNATURE, contentSignature);
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
      DosetaSignature signature = new DosetaSignature();
      signature.setTimestamp();
      signature.sign(new HashMap(), "hello world".getBytes(), keys.getPrivate());
      String sig = signature.toString();
      System.out.println("Doseta-Signature: " + sig);
      signature = new DosetaSignature(sig);

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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.getEntity();
         throw new Exception("unreachable!");
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.getEntity();
         throw new Exception("unreachable!");
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
      Assert.assertNotNull(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());
      try
      {
         String output = response.getEntity();
         throw new Exception("unreachable!");
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
      System.out.println(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
      Assert.assertNotNull(response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE));
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
      String signatureHeader = response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Doseta-Signature:  " + signatureHeader);

      DosetaSignature contentSignature = new DosetaSignature(signatureHeader);

      MarshalledEntity<String> entity = response.getEntity(new GenericType<MarshalledEntity<String>>()
      {
      });
      boolean verified = contentSignature.verify(response.getHeaders(), entity.getMarshalledBytes(), keys.getPublic());
      Assert.assertFalse(verified);
   }

   @Test
   public void testBadHash() throws Exception
   {
      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/signed/bad-hash"));
      ClientResponse<?> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      String signatureHeader = response.getHeaders().getFirst(DosetaSignature.DOSETA_SIGNATURE);
      Assert.assertNotNull(signatureHeader);
      System.out.println("Doseta-Signature:  " + signatureHeader);

      DosetaSignature contentSignature = new DosetaSignature(signatureHeader);

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
