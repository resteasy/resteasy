package org.jboss.resteasy.security;


import org.bouncycastle.x509.X509V1CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class KeyTools
{
   static
   {
      BouncyIntegration.init();
   }

   public static X509Certificate generateTestCertificate(KeyPair pair) throws InvalidKeyException,
           NoSuchProviderException, SignatureException
   {

      X509V1CertificateGenerator certGen = new X509V1CertificateGenerator();

      certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
      certGen.setIssuerDN(new X500Principal("CN=Test Certificate"));
      certGen.setNotBefore(new Date(System.currentTimeMillis() - 10000));
      certGen.setNotAfter(new Date(System.currentTimeMillis() + 10000));
      certGen.setSubjectDN(new X500Principal("CN=Test Certificate"));
      certGen.setPublicKey(pair.getPublic());
      certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

      return certGen.generateX509Certificate(pair.getPrivate(), "BC");
   }
}
