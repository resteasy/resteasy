package org.jboss.resteasy.test.security.smime;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import junit.framework.Assert;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.security.PemUtils;
import org.jboss.resteasy.security.smime.EnvelopedInput;
import org.jboss.resteasy.security.smime.EnvelopedOutput;
import org.jboss.resteasy.security.smime.SignedInput;
import org.jboss.resteasy.security.smime.SignedOutput;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * RESTEASY-962
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date April 10, 2015
 */
public class VerifyDecryptTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   protected static final MediaType MULTIPART_MIXED = new MediaType("multipart", "mixed");
   
   protected static X509Certificate cert;
   protected static PrivateKey privateKey;
   
   @Path("/")
   public static class TestResource
   {
      @POST
      @Path("encrypt")
      public String encrypt(EnvelopedInput<String> input) throws Exception
      {
         String secret = input.getEntity(privateKey, cert);
         System.out.println("secret: " + secret);
         return secret;
      }
      
      @POST
      @Path("sign")
      public String sign(SignedInput<String> input) throws Exception
      {
         if (!input.verify(cert))
         {
            throw new WebApplicationException(500);
         }
         String secret = input.getEntity();
         System.out.println("secret: " + secret);
         return secret;
      }
      
      @POST
      @Path("encryptSign")
      public String encryptSign(SignedInput<EnvelopedInput<String>> input) throws Exception
      {
         if (!input.verify(cert))
         {
            throw new WebApplicationException(500);
         }
         final EnvelopedInput<String> envelop = input.getEntity();
         String secret = envelop.getEntity(privateKey, cert);
         System.out.println("secret: " + secret);
         return secret;
      }
      
      @POST
      @Path("signEncrypt")
      public String signEncrypt(EnvelopedInput<SignedInput<String>> input) throws Exception
      {
         SignedInput<String> signedInput = input.getEntity(privateKey, cert);
         
         if (!signedInput.verify(cert))
         {
            throw new WebApplicationException(500);
         }
         String secret = signedInput.getEntity();
         System.out.println("secret: " + secret);
         return secret;
      }

      @Path("encryptedEncrypted")
      @POST
      public String encryptedEncrypted(EnvelopedInput<EnvelopedInput<String>> input) throws Exception
      {
         EnvelopedInput<String> envelope = input.getEntity(privateKey, cert);
         String secret = envelope.getEntity(privateKey, cert);
         System.out.println("secret: " + secret);
         return secret;
      }  
   
      @Path("encryptSignSign")
      @POST
      public String encryptSignSign(SignedInput<SignedInput<EnvelopedInput<String>>> input) throws Exception
      {
         if (!input.verify(cert))
         {
            throw new WebApplicationException(500);
         }
         SignedInput<EnvelopedInput<String>> inner = input.getEntity();
         if (!inner.verify(cert))
         {
            throw new WebApplicationException(500);
         }
         final EnvelopedInput<String> envelop = inner.getEntity();
         String secret = envelop.getEntity(privateKey, cert);
         System.out.println("secret: " + secret);
         return secret;
      }
      
      @Path("multipartEncrypted")
      @POST
      public String post(EnvelopedInput<MultipartInput> input) throws Exception
      {
         MultipartInput multipart = input.getEntity(privateKey, cert);
         InputPart inputPart = multipart.getParts().iterator().next();
         String secret = inputPart.getBody(String.class, null);
         System.out.println("secret: " + secret);
         return secret;
      }
   }

   @Before
   public void before() throws Exception
   {
      InputStream certIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert.pem");
      cert = PemUtils.decodeCertificate(certIs);
      InputStream privateIs = Thread.currentThread().getContextClassLoader().getResourceAsStream("mycert-private.pem");
      privateKey = PemUtils.decodePrivateKey(privateIs);
      
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testEncrypt() throws Exception
   {
      EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      output.setCertificate(cert);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/encrypt");
      Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
   
   @Test
   public void testSign() throws Exception
   {
      SignedOutput signed = new SignedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      signed.setPrivateKey(privateKey);
      signed.setCertificate(cert);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/sign");
      Response res = target.request().post(Entity.entity(signed, "multipart/signed"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
   
   @Test
   public void testEncryptSign() throws Exception
   {
      EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      output.setCertificate(cert);
      SignedOutput signed = new SignedOutput(output, "application/pkcs7-mime");
      signed.setCertificate(cert);
      signed.setPrivateKey(privateKey);    
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/encryptSign");
      Response res = target.request().post(Entity.entity(signed,"multipart/signed"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
   
   @Test
   public void testSignEncrypt() throws Exception
   {
      SignedOutput signed = new SignedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      signed.setPrivateKey(privateKey);
      signed.setCertificate(cert);
      EnvelopedOutput output = new EnvelopedOutput(signed, "multipart/signed");
      output.setCertificate(cert);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/signEncrypt");
      Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }

   @Test
   public void testEncryptedEncrypted()
   {
      MultipartOutput multipart = new MultipartOutput();
      multipart.addPart("xanadu", MediaType.TEXT_PLAIN_TYPE);
      
      EnvelopedOutput innerPart = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      innerPart.setCertificate(cert);
      
      EnvelopedOutput output = new EnvelopedOutput(innerPart, "application/pkcs7-mime");
      output.setCertificate(cert);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/encryptedEncrypted");
      Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
   
   @Test
   public void testEncryptSignSign() throws Exception
   {
      EnvelopedOutput output = new EnvelopedOutput("xanadu", MediaType.TEXT_PLAIN_TYPE);
      output.setCertificate(cert);
      SignedOutput signed = new SignedOutput(output, "application/pkcs7-mime");
      signed.setCertificate(cert);
      signed.setPrivateKey(privateKey);
      SignedOutput resigned = new SignedOutput(signed, "multipart/signed");
      resigned.setCertificate(cert);
      resigned.setPrivateKey(privateKey);      
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/encryptSignSign");
      Response res = target.request().post(Entity.entity(resigned,"multipart/signed"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
   
   @Test
   public void testMultipartEncrypted()
   {
      MultipartOutput multipart = new MultipartOutput();
      multipart.addPart("xanadu", MediaType.TEXT_PLAIN_TYPE);
      EnvelopedOutput output = new EnvelopedOutput(multipart, MULTIPART_MIXED);
      output.setCertificate(cert);
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target("http://localhost:8081/multipartEncrypted");
      Response res = target.request().post(Entity.entity(output, "application/pkcs7-mime"));
      String result = res.readEntity(String.class);
      System.out.println("result: " + result);
      Assert.assertEquals("xanadu", result);
   }
}
