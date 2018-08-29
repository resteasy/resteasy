package org.jboss.resteasy.jose.jwe.crypto;


import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.Algorithm;
import org.jboss.resteasy.jose.jwe.CompressionAlgorithm;
import org.jboss.resteasy.jose.jwe.EncryptionMethod;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;


/**
 * RSA encrypter
 * <p>Supports the following JWE algorithms:
 * </p>
 * <ul>
 * <li>RSA1_5
 * <li>RSA_OAEP
 * </ul>
 * <p>Supports the following encryption methods:
 * </p>
 * <ul>
 * <li>A128CBC_HS256
 * <li>A256CBC_HS512
 * <li>A128GCM
 * <li>A256GCM
 * </ul>
 *
 * @author David Ortiz
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-29)
 */
public class RSAEncrypter
{


   /**
    * Random byte generator.
    */
   private static SecureRandom randomGen;


   /**
    * Initialises the secure random byte generator.
    *
    * @throws RuntimeException If the secure random byte generator couldn't
    *                          be instantiated.
    */
   private static void initSecureRandom()
   {

      try
      {
         randomGen = SecureRandom.getInstance("SHA1PRNG");

      }
      catch (NoSuchAlgorithmException e)
      {

         throw new RuntimeException(e.getMessage(), e);
      }
   }

   public static String encrypt(Algorithm alg, EncryptionMethod enc, CompressionAlgorithm compressionAlgorithm, RSAPublicKey publicKey, String encodedJWEHeader, byte[] bytes)
   {

      if (randomGen == null) initSecureRandom();

      // Generate and encrypt the CEK according to the enc method
      SecretKey cek = AES.generateKey(enc.getCekBitLength());

      String encryptedKey = null; // The second JWE part

      if (alg.equals(Algorithm.RSA1_5))
      {

         encryptedKey = Base64Url.encode(RSA1_5.encryptCEK(publicKey, cek));

      }
      else if (alg.equals(Algorithm.RSA_OAEP))
      {

         encryptedKey = Base64Url.encode(RSA_OAEP.encryptCEK(publicKey, cek));

      }
      else
      {

         throw new RuntimeException(Messages.MESSAGES.unsupportedJWEalgorithm());
      }


      // Apply compression if instructed
      byte[] plainText = DeflateHelper.applyCompression(compressionAlgorithm, bytes);

      // Compose the AAD
      byte[] aad = encodedJWEHeader.getBytes(StandardCharsets.UTF_8);

      // Encrypt the plain text according to the JWE enc
      byte[] iv;
      AuthenticatedCipherText authCipherText;

      if (enc.equals(EncryptionMethod.A128CBC_HS256) || enc.equals(EncryptionMethod.A256CBC_HS512))
      {

         iv = AESCBC.generateIV(randomGen);

         authCipherText = AESCBC.encryptAuthenticated(cek, iv, plainText, aad);

      }
      else if (enc.equals(EncryptionMethod.A128GCM) || enc.equals(EncryptionMethod.A256GCM))
      {

         iv = AESGCM.generateIV(randomGen);

         authCipherText = AESGCM.encrypt(cek, iv, plainText, aad);

      }
      else
      {

         throw new RuntimeException(Messages.MESSAGES.unsupportedEncryptionMethod());
      }
      StringBuilder builder = new StringBuilder(encodedJWEHeader)
              .append('.').append(encryptedKey)
              .append('.').append(Base64Url.encode(iv))
              .append('.').append(Base64Url.encode(authCipherText.getCipherText()))
              .append('.').append(Base64Url.encode(authCipherText.getAuthenticationTag()));

      return builder.toString();
   }
}