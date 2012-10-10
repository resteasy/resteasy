package org.jboss.resteasy.tests.signature;

import org.jboss.resteasy.annotations.security.doseta.Signed;
import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.security.doseta.UnauthorizedSignatureException;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.security.doseta.Verifier;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
   public static ResteasyClient client;

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
      client = new ResteasyClient();
   }

   @AfterClass
   public static void shutdown() throws Exception
   {

   }

   @Test
   public void testVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      WebTarget target = client.target("http://localhost:9095/signed");
      Invocation.Builder request = target.request();
      request.configuration().setProperty(Verifier.class.getName(), verifier);
      Response response = request.get();

      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testFailedVerification() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      WebTarget target = client.target("http://localhost:9095/signed/bad-signature");
      Invocation.Builder request = target.request();
      request.configuration().setProperty(Verifier.class.getName(), verifier);
      Response response = request.get();

      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      try
      {
         System.out.println(response.readEntity(String.class));
         throw new RuntimeException("UNREACHABLE!!!");
      }
      catch (UnauthorizedSignatureException e)
      {
         System.out.println("We expect this failure: " + e.getMessage());

      }
      response.close();

   }


   @Test
   public void testPost() throws Exception
   {
      WebTarget target = client.target("http://localhost:9095/signed");
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("test");
      contentSignature.setDomain("samplezone.org");
      Invocation.Builder request = target.request();
      request.configuration().setProperty(KeyRepository.class.getName(), repository);

      request.header(DKIMSignature.DKIM_SIGNATURE, contentSignature);
      Response response = request.post(Entity.text("hello world"));
      Assert.assertEquals(204, response.getStatus());
      response.close();
   }

   @Test
   public void testExpiresMinutes() throws Exception
   {
      Verifier verifier = new Verifier();
      Verification verification = verifier.addNew();
      verification.setRepository(repository);

      WebTarget target = client.target("http://localhost:9095/signed/expires-minute");
      Invocation.Builder request = target.request();
      request.configuration().setProperty(Verifier.class.getName(), verifier);
      Response response = request.get();

      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());

      // If you don't extract the entity, then verification will not happen
      System.out.println(response.readEntity(String.class));
      response.close();
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

      WebTarget target = client.target("http://localhost:9095/signed/expires-short");
      Invocation.Builder request = target.request();
      request.configuration().setProperty(Verifier.class.getName(), verifier);
      Response response = request.get();

      System.out.println(response.getHeaderString(DKIMSignature.DKIM_SIGNATURE));
      Assert.assertEquals(200, response.getStatus());
      Thread.sleep(1500);
      try
      {
         String output = response.readEntity(String.class);
         throw new Exception("unreachable!");
      }
      catch (UnauthorizedSignatureException e)
      {
         System.out.println("Verification failed: " + e.getMessage());
      }
      response.close();


   }

   @Test
   public void testProxy() throws Exception
   {
      ResteasyWebTarget target = client.target("http://localhost:9095");
      target.configuration().setProperty(KeyRepository.class.getName(), repository);
      SigningProxy proxy = target.proxy(SigningProxy.class);
      String output = proxy.hello();
      proxy.postSimple("hello world");
   }

}
