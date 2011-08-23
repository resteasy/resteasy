package org.jboss.resteasy.security;

import org.jboss.resteasy.util.Base64;

import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PemUtils
{
   public static X509Certificate getCertificateFromDer(InputStream is) throws Exception
   {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate cert = (X509Certificate)cf.generateCertificate(is);
      is.close();
      return cert;
   }
   public static PrivateKey getPrivateFromDer(InputStream is)
           throws Exception
   {

      DataInputStream dis = new DataInputStream(is);
      byte[] keyBytes = new byte[dis.available()];
      dis.readFully(keyBytes);
      dis.close();

      PKCS8EncodedKeySpec spec =
              new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
   }

   /**
    * Extract a public key from a PEM string
    *
    * @param pem
    * @return
    * @throws Exception
    */
   public static PublicKey extractPublicKey(String pem) throws Exception
   {
      pem = pem.replace("-----BEGIN PUBLIC KEY-----", "");
      pem = pem.replace("-----END PUBLIC KEY-----", "");
      byte[] der = Base64.decode(pem);


      X509EncodedKeySpec spec =
              new X509EncodedKeySpec(der);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(spec);
   }

   /**
    * Extract a private key that is a PKCS8 pem string (base64 encoded PKCS8)
    *
    * @param pem
    * @return
    * @throws Exception
    */
   public static PrivateKey extractPrivateKey(String pem) throws Exception
   {
      pem = pem.replace("-----BEGIN PRIVATE KEY-----", "");
      pem = pem.replace("-----END PRIVATE KEY-----", "");
      byte[] der = Base64.decode(pem);


      PKCS8EncodedKeySpec spec =
              new PKCS8EncodedKeySpec(der);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(spec);
   }
}
