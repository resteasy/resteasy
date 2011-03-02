package org.jboss.resteasy.test.security.signing;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */

import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

public class ExampleSignTest
{
   @Test
   public void testSign() throws Exception
   {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(1024);
      KeyPair keyPair = kpg.genKeyPair();

      byte[] data = "test".getBytes("UTF8");

      Signature sig = Signature.getInstance("MD5WithRSA");
      sig.initSign(keyPair.getPrivate());
      sig.update(data);
      byte[] signatureBytes = sig.sign();
      System.out.println("Signature:" + new BASE64Encoder().encode(signatureBytes));

      sig.initVerify(keyPair.getPublic());
      sig.update(data);

      System.out.println();
      System.out.println("Verify: " + sig.verify(signatureBytes));
   }
}