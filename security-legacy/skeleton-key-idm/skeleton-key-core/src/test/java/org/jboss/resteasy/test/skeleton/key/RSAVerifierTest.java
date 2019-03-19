package org.jboss.resteasy.test.skeleton.key;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.jboss.logging.Logger;
import org.jboss.resteasy.jose.jws.JWSBuilder;
import org.jboss.resteasy.jwt.JsonSerialization;
import org.jboss.resteasy.skeleton.key.RSATokenVerifier;
import org.jboss.resteasy.skeleton.key.ResourceMetadata;
import org.jboss.resteasy.skeleton.key.VerificationException;
import org.jboss.resteasy.skeleton.key.representations.SkeletonKeyToken;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RSAVerifierTest
{
   private static final Logger LOG = Logger.getLogger(RSAVerifierTest.class);
   private static X509Certificate[] idpCertificates;
   private static KeyPair idpPair;
   private static KeyPair badPair;
   private static KeyPair clientPair;
   private static X509Certificate[] clientCertificateChain;
   private ResourceMetadata metadata;
   private SkeletonKeyToken token;

   static
   {
      if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
   }

   public static X509Certificate generateTestCertificate(String subject, String issuer,
                  KeyPair pair) throws IOException, OperatorCreationException,
                  CertificateException {

         X500Name subjectDN = new X500Name(subject);
         X500Name    issuerDN = new X500Name(issuer);

         Date validityStartDate = new Date(System.currentTimeMillis() - 10000);
         Date validityEndDate = new Date(System.currentTimeMillis() + 10000);
         SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo
            .getInstance(pair.getPublic().getEncoded());

         X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(
            issuerDN, BigInteger.valueOf(System.currentTimeMillis()),
            validityStartDate, validityEndDate, subjectDN, subPubKeyInfo);

         X509CertificateHolder holder = certGen.build(createSigner(pair.getPrivate()));

         return new JcaX509CertificateConverter().getCertificate(holder);
   }

   private static ContentSigner createSigner(PrivateKey privateKey) throws IOException,
      OperatorCreationException {
         AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
            .find("SHA256WithRSAEncryption");
         AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
            .find(sigAlgId);

         return new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
            .build(PrivateKeyFactory.createKey(privateKey.getEncoded()));
   }

   @BeforeClass
   public static void setupCerts() throws NoSuchAlgorithmException,
      IOException, OperatorCreationException, CertificateException
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
      metadata = new ResourceMetadata();
      metadata.setResourceName("service");
      metadata.setRealm("domain");
      metadata.setRealmKey(idpPair.getPublic());

      token = new SkeletonKeyToken();
      token.principal("CN=Client")
            .audience("domain")
            .addAccess("service").addRole("admin");
   }

   @Test
   public void testPemWriter() throws Exception
   {
      PublicKey realmPublicKey = idpPair.getPublic();
      StringWriter sw = new StringWriter();
      PEMWriter writer = new PEMWriter(sw);
      try
      {
         writer.writeObject(realmPublicKey);
         writer.flush();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      LOG.info(sw.toString());
   }


   @Test
   public void testSimpleVerification() throws Exception
   {

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
            .content(tokenBytes)
            .rsa256(idpPair.getPrivate());
      SkeletonKeyToken token =  RSATokenVerifier.verifyToken(encoded, metadata);
      Assert.assertTrue(token.getResourceAccess("service").getRoles().contains("admin"));
      Assert.assertEquals("CN=Client", token.getPrincipal());
   }

   /*
   @Test
   public void testSpeed() throws Exception
   {

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
            .content(tokenBytes)
            .rsa256(idpPair.getPrivate());

      long start = System.currentTimeMillis();
      int count = 10000;
      for (int i = 0; i < count; i++)
      {
         SkeletonKeyTokenVerification v = RSATokenVerifier.verify(null, encoded, metadata);

      }
      long end = System.currentTimeMillis() - start;
      System.out.println("rate: " + ((double)end/(double)count));
   }
    */


   @Test
   public void testBadSignature() throws Exception
   {

      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
            .content(tokenBytes)
            .rsa256(badPair.getPrivate());

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
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

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
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

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
         LOG.info(ignored.getMessage());
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

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
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

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
         Assert.fail();
      }
      catch (VerificationException ignored)
      {
         LOG.info(ignored.getMessage());
      }
   }

   @Test
   public void testTokenAuth() throws Exception
   {
      token = new SkeletonKeyToken();
      token.principal("CN=Client")
            .audience("domain")
            .addAccess("service").addRole("admin").verifyCaller(true);
      byte[] tokenBytes = JsonSerialization.toByteArray(token, false);

      String encoded = new JWSBuilder()
            .content(tokenBytes)
            .rsa256(idpPair.getPrivate());

      SkeletonKeyToken v = null;
      try
      {
         v = RSATokenVerifier.verifyToken(encoded, metadata);
      }
      catch (VerificationException ignored)
      {
         LOG.info(ignored.getMessage());
      }
   }



}
