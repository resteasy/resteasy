package org.jboss.resteasy.jose.jwe.crypto;


import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.Algorithm;
import org.jboss.resteasy.jose.jwe.CompressionAlgorithm;
import org.jboss.resteasy.jose.jwe.EncryptionMethod;

import javax.crypto.SecretKey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * Direct encrypter with a
 * shared symmetric key. This class is thread-safe.
 * <p>Supports the following JWE algorithms:
 * </p>
 * <ul>
 * <li>DIR
 * </ul>
 * <p>Supports the following encryption methods:
 * </p>
 * <ul>
 * <li>A128CBC_HS256}
 * <li>A256CBC_HS512}
 * <li>A128GCM}
 * <li>A256GCM}
 * </ul>
 *
 * @author Vladimir Dzhuvinov
 * @version $version$ (2013-05-29)
 */
public class DirectEncrypter
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


   public static String encrypt(EncryptionMethod enc, CompressionAlgorithm compressionAlgorithm, String encodedJWEHeader, final SecretKey key, final byte[] bytes)
   {

      if (randomGen == null) initSecureRandom();

      if (enc.getCekBitLength() != key.getEncoded().length * 8)
      {

         throw new RuntimeException(Messages.MESSAGES.contentEncryptionKeyLength(enc.getCekBitLength(), enc));
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

         authCipherText = AESCBC.encryptAuthenticated(key, iv, plainText, aad);

      }
      else if (enc.equals(EncryptionMethod.A128GCM) || enc.equals(EncryptionMethod.A256GCM))
      {

         iv = AESGCM.generateIV(randomGen);

         authCipherText = AESGCM.encrypt(key, iv, plainText, aad);

      }
      else
      {

         throw new RuntimeException(Messages.MESSAGES.unsupportedEncryptionMethod());
      }

      StringBuilder builder = new StringBuilder(encodedJWEHeader)
              .append('.')
              .append('.').append(Base64Url.encode(iv))
              .append('.').append(Base64Url.encode(authCipherText.getCipherText()))
              .append('.').append(Base64Url.encode(authCipherText.getAuthenticationTag()));

      return builder.toString();
   }
}