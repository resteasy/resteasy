package org.jboss.resteasy.test.nextgen.security.doseta;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.security.doseta.Verify;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.KeyRepository;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VerifyAnnotationNoEntityTest extends BaseResourceTest
{
   public static KeyPair keys;
   public static DosetaKeyRepository repository;
   public static PrivateKey badKey;
   public static ResteasyClient client;

   @BeforeClass
   public static void setup() throws Exception
   {
      Logger.setLoggerType(Logger.LoggerType.JUL);
      repository = new DosetaKeyRepository();
      repository.setKeyStorePath("test.jks");
      repository.setKeyStorePassword("password");
      repository.setUseDns(false);
      repository.start();

      PrivateKey privateKey = repository.getKeyStore().getPrivateKey("test._domainKey.samplezone.org");
      if (privateKey == null) throw new Exception("Private Key is null!!!");
      PublicKey publicKey = repository.getKeyStore().getPublicKey("test._domainKey.samplezone.org");
      keys = new KeyPair(publicKey, privateKey);

      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      badKey = keyPair.getPrivate();

      dispatcher.getDefaultContextObjects().put(KeyRepository.class, repository);
      addPerRequestResource(SignedResource.class);
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void afterIt() throws Exception
   {
      client.close();
   }


   @Path("/signed")
   public static class SignedResource
   {
      @GET
      @Path("nobody")
      @Consumes("text/plain")
      @Verify(bodyHashRequired=false)
      public String get(@HeaderParam(DKIMSignature.DKIM_SIGNATURE) DKIMSignature signature)
      {
         Assert.assertNotNull(signature);
         return "xyz";
      }
   }

   @Test
   public void testBasicVerificationBadSignatureNoBody() throws Exception
   {
      WebTarget target = client.target(TestPortProvider.generateURL("/signed/nobody"));
      DKIMSignature contentSignature = new DKIMSignature();
      contentSignature.setSelector("test");
      contentSignature.setDomain("samplezone.org");
      contentSignature.setPrivateKey(badKey);
      Response response = target.request().header(DKIMSignature.DKIM_SIGNATURE, contentSignature).get();
      Assert.assertEquals(401, response.getStatus());
      response.close();
   }
}
