package org.jboss.resteasy.security;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v1CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class provides utility functions for generation of V1
 * {@link java.security.cert.X509Certificate}
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 2 $
 */
public class KeyTools {
   static {
      BouncyIntegration.init();
   }

   /**
    * Generate version 1 self signed {@link java.security.cert.X509Certificate}
    *
    * @param pair the CA key pair
    * @return the x509 certificate
    */
   public static X509Certificate generateTestCertificate(KeyPair pair) {
      return generateV1SelfSignedCertificate(pair, "Test", null);
   }

   /**
    * Generate version 1 self signed {@link java.security.cert.X509Certificate}
    *
    * @param subject the subject name
    * @param issuer the issuer name
    * @param pair the CA key pair
    * @return the x509 certificate
    */
   public static X509Certificate generateTestCertificate(String subject,
                                                         String issuer, KeyPair pair) {
      return generateV1SelfSignedCertificate(pair, subject,issuer);
   }

   /**
    * Generate version 1 self signed {@link java.security.cert.X509Certificate}
    *
    * @param caKeyPair the CA key pair
    * @param subject the subject name
    * @param issuer the issuer name
    * @return the x509 certificate
    * @throws RuntimeException
    */
   private static X509Certificate generateV1SelfSignedCertificate(KeyPair caKeyPair,
                                                         String subject, String issuer) {
      try {
         X500Name subjectDN = new X500Name("CN=" + subject);
         X500Name issuerDN;
         if (issuer == null) {
            issuerDN = subjectDN;
         } else {
            issuerDN = new X500Name("CN=" + issuer);
         }

         Date validityStartDate = new Date(System.currentTimeMillis() - 10000);
         Date validityEndDate = new Date(System.currentTimeMillis() + 10000);
         SubjectPublicKeyInfo subPubKeyInfo = SubjectPublicKeyInfo
            .getInstance(caKeyPair.getPublic().getEncoded());

         X509v1CertificateBuilder certGen = new X509v1CertificateBuilder(
            issuerDN, BigInteger.valueOf(System.currentTimeMillis()),
            validityStartDate, validityEndDate, subjectDN, subPubKeyInfo);

         X509CertificateHolder holder = certGen.build(createSigner(caKeyPair.getPrivate()));

         return new JcaX509CertificateConverter().getCertificate(holder);

      }catch (Exception e) {
         throw new RuntimeException("Error creating X509v1Certificate.", e);
      }
   }

   /**
    * Creates the content signer for generation of Version 1
    * {@link java.security.cert.X509Certificate}.
    *
    * @param privateKey the private key
    * @return the content signer
    * @throws RuntimeException
    */
   private static ContentSigner createSigner(PrivateKey privateKey) {
      try {
         AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder()
            .find("SHA256WithRSAEncryption");
         AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder()
            .find(sigAlgId);

         return new BcRSAContentSignerBuilder(sigAlgId, digAlgId)
            .build(PrivateKeyFactory.createKey(privateKey.getEncoded()));
      } catch (Exception e) {
         throw new RuntimeException("Could not create content signer.", e);
      }
   }
}
