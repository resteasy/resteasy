package org.jboss.resteasy.test.security.doseta;

import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.security.doseta.DKIMSignature;
import org.jboss.resteasy.security.doseta.DosetaKeyRepository;
import org.jboss.resteasy.security.doseta.Verification;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LocalTest
{
   public static KeyPair keys;
   public static DosetaKeyRepository repository;


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
   }

   @Test
   public void testAttributes() throws Exception
   {
      DKIMSignature signed = new DKIMSignature();
      signed.setAttribute("path", "/hello/world");
      signed.setTimestamp();
      signed.addHeader("Visa");
      signed.addHeader("Visa");
      MultivaluedMapImpl<String, String> headers = new MultivaluedMapImpl<String, String>();
      headers.add("Visa", "v1");
      headers.add("Visa", "v2");
      headers.add("Visa", "v3");
      signed.sign(headers, null, keys.getPrivate());

      String signedHeader = signed.toString();

      System.out.println(signedHeader);

      DKIMSignature verified = new DKIMSignature(signedHeader);

      HashMap<String, String> requiredAttributes = new HashMap<String, String>();
      requiredAttributes.put("path", "/hello/world");

      Verification verification = new Verification();
      verification.setBodyHashRequired(false);
      verification.getRequiredAttributes().put("path", "/hello/world");

      MultivaluedMap<String, String> verifiedHeaders = verified.verify(headers, null, keys.getPublic(), verification);
      Assert.assertEquals(verifiedHeaders.size(), 1);
      List<String> visas = verifiedHeaders.get("Visa");
      Assert.assertNotNull(visas);
      Assert.assertEquals(visas.size(), 2);
      System.out.println(visas);
      Assert.assertEquals(visas.get(0), "v3");
      Assert.assertEquals(visas.get(1), "v2");
   }

   @Test
   public void testBadAttributes() throws Exception
   {
      DKIMSignature signed = new DKIMSignature();
      signed.setAttribute("path", "/hello/world");
      signed.setTimestamp();
      signed.addHeader("Visa");
      signed.addHeader("Visa");
      MultivaluedMapImpl<String, String> headers = new MultivaluedMapImpl<String, String>();
      headers.add("Visa", "v1");
      headers.add("Visa", "v2");
      headers.add("Visa", "v3");
      signed.sign(headers, null, keys.getPrivate());

      String signedHeader = signed.toString();

      System.out.println(signedHeader);

      DKIMSignature verified = new DKIMSignature(signedHeader);

      HashMap<String, String> requiredAttributes = new HashMap<String, String>();
      requiredAttributes.put("path", "/hello/world");

      Verification verification = new Verification();
      verification.setBodyHashRequired(false);
      verification.getRequiredAttributes().put("path", "/hello");

      MultivaluedMap<String, String> verifiedHeaders = null;
      try
      {
         verifiedHeaders = verified.verify(headers, null, keys.getPublic(), verification);
         Assert.fail("should fail");
      }
      catch (SignatureException e)
      {
      }
   }
}
