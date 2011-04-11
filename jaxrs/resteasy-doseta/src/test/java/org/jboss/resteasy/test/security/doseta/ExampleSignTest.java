package org.jboss.resteasy.test.security.doseta;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

import org.jboss.resteasy.util.Hex;
import org.junit.Assert;
import org.junit.Test;
//import sun.security.rsa.RSASignature;
//import sun.security.x509.AlgorithmId;

//import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;

public class ExampleSignTest
{
   /*
   @Test
   public void testDerFile() throws Exception
   {
      // import private key
      InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("src/test/resources/mycert-private.der");
      Assert.assertNotNull(is);
      DataInputStream dis = new DataInputStream(is);
      byte[] derFile = new byte[dis.available()];
      dis.readFully(derFile);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(derFile);
      PrivateKey privateKey = kf.generatePrivate(spec);


      Signature instance = Signature.getInstance("SHA256withRSA");
      instance.initSign(privateKey);
      instance.update("from-java".getBytes());
      byte[] signatureBytes = instance.sign();
      System.out.println("Signature: ");
      System.out.println(Hex.encodeHex(signatureBytes));


      // import public key
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream("src/test/resources/mycert.der");
      Certificate cert = cf.generateCertificate(is);
      PublicKey publicKey = cert.getPublicKey();



      String pythonHexSignature = "4e3014a3a0ff296c07927e846221ee68f70e0b06ed54a1fe974944ea17b836b92279635a7e0bb6b8923df94f4023de95ef07fa76506888897a88ac440eb185b6b117f4c906cba989ffb4e1f81c6677db12e7dc22d51d9369df92165709817792dc3e647dae6b70a0d84c386b0228c2442c9a6a0107381aac8e4cb4c367435d52";
      // loading CertificateChain
      Signature verify = Signature.getInstance("SHA256withRSA");
      verify.initVerify(publicKey);
      verify.update("from-python".getBytes());
      Assert.assertTrue(verify.verify(Hex.decodeHex(pythonHexSignature)));


   }
   */

   /* commented out, this is just some code that recreates what java.security.Signature does for RSA
   @Test
   public void testRecreate() throws Exception
   {
      KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
      PrivateKey privateKey = keyPair.getPrivate();
      String plaintext = "This is the message being signed";

// Compute signature
      Signature instance = Signature.getInstance("SHA256withRSA");
      instance.initSign(privateKey);
      instance.update((plaintext).getBytes());
      byte[] signature = instance.sign();

// Compute digest
      MessageDigest sha1 = MessageDigest.getInstance("SHA-256");
      byte[] digest = sha1.digest((plaintext).getBytes());

// Encrypt digest
      Cipher cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);

      byte[] cipherText = cipher.doFinal(RSASignature.encodeSignature(AlgorithmId.SHA256_oid, digest));

// Display results
      System.out.println("Input data: " + plaintext);
      System.out.println("Digest: " + Hex.encodeHex(digest));
      System.out.println("Cipher text: " + Hex.encodeHex(cipherText));
      System.out.println("Signature: " + Hex.encodeHex(signature));

   }
   */
}