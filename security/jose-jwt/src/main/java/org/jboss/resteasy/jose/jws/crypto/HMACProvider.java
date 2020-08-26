package org.jboss.resteasy.jose.jws.crypto;

import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jws.Algorithm;
import org.jboss.resteasy.jose.jws.JWSInput;
import org.jboss.resteasy.jose.jws.util.Base64Url;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HMACProvider implements SignatureProvider
{
   public static String getJavaAlgorithm(Algorithm alg)
   {
      switch (alg)
      {
         case HS256:
            return "HMACSHA256";
         case HS384:
            return "HMACSHA384";
         case HS512:
            return "HMACSHA512";
         default:
            throw new IllegalArgumentException(Messages.MESSAGES.notAMACalgorithm());
      }
   }

   public static Mac getMAC(final Algorithm alg)
   {

      try
      {
         return javax.crypto.Mac.getInstance(getJavaAlgorithm(alg));
      }
      catch (NoSuchAlgorithmException e)
      {
         throw new RuntimeException(Messages.MESSAGES.unsupportedHMACalgorithm(e.getLocalizedMessage()), e);
      }
   }

   public static byte[] sign(byte[] data, Algorithm algorithm, byte[] sharedSecret)
   {
      try
      {
         Mac mac = HMACProvider.getMAC(algorithm);
         mac.init(new SecretKeySpec(sharedSecret, mac.getAlgorithm()));
         mac.update(data);
         return mac.doFinal();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public static byte[] sign(byte[] data, Algorithm algorithm, SecretKey key)
   {
      try
      {
         Mac mac = HMACProvider.getMAC(algorithm);
         mac.init(key);
         mac.update(data);
         return mac.doFinal();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   public static boolean verify(JWSInput input, SecretKey key)
   {
      try
      {
         byte[] signature = sign(input.getEncodedSignatureInput().getBytes(StandardCharsets.UTF_8), input.getHeader().getAlgorithm(), key);
         return MessageDigest.isEqual(signature, Base64Url.decode(input.getEncodedSignature()));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }



   public static boolean verify(JWSInput input, byte[] sharedSecret)
   {
      try
      {
         byte[] signature = sign(input.getEncodedSignatureInput().getBytes(StandardCharsets.UTF_8), input.getHeader().getAlgorithm(), sharedSecret);
         return MessageDigest.isEqual(signature, Base64Url.decode(input.getEncodedSignature()));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public boolean verify(JWSInput input, String key) {
      return false;
   }
}
