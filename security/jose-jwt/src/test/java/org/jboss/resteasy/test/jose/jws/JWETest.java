package org.jboss.resteasy.test.jose.jws;

import org.jboss.resteasy.jose.jwe.JWEBuilder;
import org.jboss.resteasy.jose.jwe.JWEInput;
import org.junit.Assert;
import org.junit.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWETest
{

   @Test
   public void testRSA() throws Exception
   {
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();

      String content = "Live long and prosper.";

      {
      String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA1_5((RSAPublicKey)keyPair.getPublic());
      System.out.println("encoded: " + encoded);
      byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey)keyPair.getPrivate()).getRawContent();
      String from = new String(raw);
      Assert.assertEquals(content, from);
      }
      {
         String encoded = new JWEBuilder().contentBytes(content.getBytes()).RSA_OAEP((RSAPublicKey)keyPair.getPublic());
         System.out.println("encoded: " + encoded);
         byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey)keyPair.getPrivate()).getRawContent();
         String from = new String(raw);
         Assert.assertEquals(content, from);
      }
      {
         String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256().RSA1_5((RSAPublicKey)keyPair.getPublic());
         System.out.println("encoded: " + encoded);
         byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey)keyPair.getPrivate()).getRawContent();
         String from = new String(raw);
         Assert.assertEquals(content, from);
      }
      {
         String encoded = new JWEBuilder().contentBytes(content.getBytes()).A128CBC_HS256().RSA_OAEP((RSAPublicKey)keyPair.getPublic());
         System.out.println("encoded: " + encoded);
         byte[] raw = new JWEInput(encoded).decrypt((RSAPrivateKey)keyPair.getPrivate()).getRawContent();
         String from = new String(raw);
         Assert.assertEquals(content, from);
      }
   }

   @Test
   public void testDirect() throws Exception
   {
      String content = "Live long and prosper.";
      String encoded = new JWEBuilder().contentBytes(content.getBytes()).dir("geheim");
      System.out.println("encoded: " + encoded);
      byte[] raw = new JWEInput(encoded).decrypt("geheim").getRawContent();
      String from = new String(raw);
      Assert.assertEquals(content, from);

   }
}
