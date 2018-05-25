package org.jboss.resteasy.jose.jwe.crypto;


import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.Algorithm;
import org.jboss.resteasy.jose.jwe.EncryptionMethod;
import org.jboss.resteasy.jose.jwe.JWEHeader;

import javax.crypto.SecretKey;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;


/**
 * RSA decrypter. This class
 * is thread-safe.
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
public class RSADecrypter
{

   public static byte[] decrypt(final JWEHeader readOnlyJWEHeader,
                                final String encodedHeader,
                                final String encodedEncryptedKey,
                                final String encodedIv,
                                final String encodedCipherText,
                                final String encodedAuthTag,
                                final RSAPrivateKey privateKey
                               )
   {

      // Validate required JWE parts
      if (encodedEncryptedKey == null)
      {

         throw new RuntimeException(Messages.MESSAGES.encryptedKeyMustNotBeNull());
      }

      if (encodedIv == null)
      {

         throw new RuntimeException(Messages.MESSAGES.initializationVectorMustNotBeNull());
      }

      if (encodedAuthTag == null)
      {

         throw new RuntimeException(Messages.MESSAGES.authenticationTagMustNotBeNull());
      }


      // Derive the content encryption key
      Algorithm alg = readOnlyJWEHeader.getAlgorithm();

      SecretKey cek = null;
      byte[] encryptedKey = Base64Url.decode(encodedEncryptedKey);
      byte[] aad = encodedHeader.getBytes(StandardCharsets.UTF_8);
      byte[] iv = Base64Url.decode(encodedIv);
      byte[] cipherText = Base64Url.decode(encodedCipherText);
      byte[] authTag = Base64Url.decode(encodedAuthTag);

      if (alg.equals(Algorithm.RSA1_5))
      {

         int keyLength = readOnlyJWEHeader.getEncryptionMethod().getCekBitLength();

         SecretKey randomCEK = AES.generateKey(keyLength);

         try
         {
            cek = RSA1_5.decryptCEK(privateKey, encryptedKey, keyLength);

         }
         catch (Exception e)
         {

            // Protect against MMA attack by generating random CEK on failure,
            // see http://www.ietf.org/mail-archive/web/jose/current/msg01832.html
            cek = randomCEK;
         }

      }
      else if (alg.equals(Algorithm.RSA_OAEP))
      {

         cek = RSA_OAEP.decryptCEK(privateKey, encryptedKey);

      }
      else
      {

         throw new RuntimeException(Messages.MESSAGES.unsupportedJWEalgorithm());
      }


      // Decrypt the cipher text according to the JWE enc
      EncryptionMethod enc = readOnlyJWEHeader.getEncryptionMethod();

      byte[] plainText;

      if (enc.equals(EncryptionMethod.A128CBC_HS256) || enc.equals(EncryptionMethod.A256CBC_HS512))
      {

         plainText = AESCBC.decryptAuthenticated(cek, iv, cipherText, aad, authTag);

      }
      else if (enc.equals(EncryptionMethod.A128GCM) || enc.equals(EncryptionMethod.A256GCM))
      {

         plainText = AESGCM.decrypt(cek, iv, cipherText, aad, authTag);

      }
      else
      {

         throw new RuntimeException(Messages.MESSAGES.unsupportedEncryptionMethod());
      }


      // Apply decompression if requested
      return DeflateHelper.applyDecompression(readOnlyJWEHeader.getCompressionAlgorithm(), plainText);
   }
}

