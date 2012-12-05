package org.jboss.resteasy.test.skeleton.key;

import junit.framework.Assert;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ServiceMetadata;
import org.jboss.resteasy.skeleton.key.SkeletonKeyToken;
import org.jboss.resteasy.skeleton.key.SkeletonKeyTokenVerification;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RSAVerifierTest
{
   private static X509Certificate[] idpCertificates;
   private static KeyPair idpPair;
   private static KeyPair badPair;
   private static KeyPair clientPair;
   private static X509Certificate[] clientCertificateChain;
   private ServiceMetadata metadata;
   private SkeletonKeyToken token;

   static
   {
      if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
   }

   public static X509Certificate generateTestCertificate(String subject, String issuer, KeyPair pair) throws InvalidKeyException,
           NoSuchProviderException, SignatureException
   {

      X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

      certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
      certGen.setIssuerDN(new X500Principal(issuer));
      certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
      certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
      certGen.setSubjectDN(new X500Principal(subject));
      certGen.setPublicKey(pair.getPublic());
      certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

      return certGen.generateX509Certificate(pair.getPrivate(), "BC");
   }

   @BeforeClass
   public static void setupCerts() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException
   {
      badPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      idpPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      idpCertificates = new X509Certificate[]{generateTestCertificate("CN=IDP", "CN=IDP", idpPair)};
      clientPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      clientCertificateChain = new X509Certificate[]{generateTestCertificate("CN=Client", "CN=IDP", idpPair)};
   }

   @Before
   public void initTest()
   {
      metadata = new ServiceMetadata();
      metadata.setName("service");
      metadata.setRealm("domain");
      metadata.setRealmKey(idpPair.getPublic());

      token = new SkeletonKeyToken();
      token.principal("CN=Client")
              .audience("domain")
              .addAccess("service").addRole("admin");
   }


   @Test
   public void testSimpleVerification() throws Exception
   {

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = RSATokenVerifier.verify(null, encoded, metadata);
      Assert.assertTrue(v.getRoles().contains("admin"));
      Assert.assertEquals("CN=Client", v.getPrincipal().getName());
      Assert.assertEquals(encoded, v.getPrincipal().getToken());
   }

   @Test
   public void testBadSignature() throws Exception
   {

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(badPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
      }
   }

   @Test
   public void testNotBeforeGood() throws Exception
   {
      token.notBefore((System.currentTimeMillis()/1000) - 100);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
      }
      catch (VerificationException ignored)
      {
        throw ignored;
      }
   }

   @Test
   public void testNotBeforeBad() throws Exception
   {
      token.notBefore((System.currentTimeMillis()/1000) + 100);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
         System.out.println(ignored.getMessage());
      }
   }

   @Test
   public void testExpirationGood() throws Exception
   {
      token.expiration((System.currentTimeMillis()/1000) + 100);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
      }
      catch (VerificationException ignored)
      {
         throw ignored;
      }
   }

   @Test
   public void testExpirationBad() throws Exception
   {
      token.expiration((System.currentTimeMillis()/1000) - 100);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
         System.out.println(ignored.getMessage());
      }
   }

   @Test
   public void testTokenAuth() throws Exception
   {
      token = new SkeletonKeyToken();
      token.principal("CN=Client")
              .audience("domain")
              .addAccess("service").addRole("admin").surrogateAuthRequired(true);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
              .content(tokenBytes)
              .rsa256(idpPair.getPrivate());

      SkeletonKeyTokenVerification v = null;
      try
      {
         v = RSATokenVerifier.verify(null, encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
         System.out.println(ignored.getMessage());
      }
   }



}
