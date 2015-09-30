package org.jboss.resteasy.jose.i18n;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;
import org.jboss.logging.annotations.Message.Format;
import org.jboss.resteasy.jose.jwe.CompressionAlgorithm;
import org.jboss.resteasy.jose.jwe.EncryptionMethod;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Aug 28, 2015
 */
@MessageBundle(projectCode = "RESTEASY")
public interface Messages
{
   Messages MESSAGES = org.jboss.logging.Messages.getBundle(Messages.class);
   int BASE = 14000;

   @Message(id = BASE + 0, value = "The algorithm of the shared symmetric key must be AES")
   String algorithmOfSharedSymmetricKey();
   
   @Message(id = BASE + 5, value = "Algorithm was null")
   String algorithmWasNull();
   
   @Message(id = BASE + 10, value = "The authentication tag must not be null")
   String authenticationTagMustNotBeNull();
    
   @Message(id = BASE + 15, value = "CEK key length mismatch: {0} != {1}", format=Format.MESSAGE_FORMAT)
   String cekKeyLengthMismatch(int length1, int length2);
   
   @Message(id = BASE + 20, value = "The cipher text must not be null")
   String cipherTextMustNotBeNull();
   
   @Message(id = BASE + 25, value = "The Content Encryption Key (CEK) length must be {0} bits for {1} encryption", format=Format.MESSAGE_FORMAT)
   String contentEncryptionKeyLength(int length, EncryptionMethod method);

   @Message(id = BASE + 30, value = "Could not find MessageBodyReader for JSON")
   String couldNotFindMessageBodyReaderForJSON();
   
   @Message(id = BASE + 35, value = "Could not find MessageBodyWriter for JSON")
   String couldNotFindMessageBodyWriterForJSON();
   
   @Message(id = BASE + 40, value = "Couldn't compress plain text: %s")
   String couldntCompressPlainText(String message);

   @Message(id = BASE + 45, value = "Couldn't decompress plain text: %s")
   String couldntDecompressPlainText(String message);
   
   @Message(id = BASE + 50, value = "Couldn't decrypt Content Encryption Key (CEK): %s")
   String couldntDecryptCEK(String message);   
    
   @Message(id = BASE + 55, value = "Couldn't encrypt Content Encryption Key (CEK): %s")
   String couldntEncryptCEK(String message);
   
   @Message(id = BASE + 60, value = "Couldn't generate GCM authentication tag: %s")
   String couldntGenerateGCMAuthentication(String message);

   @Message(id = BASE + 65, value = "Couldn't validate GCM authentication tag: %s")
   String couldntValidateGCMAuthentication(String message);
   
   @Message(id = BASE + 70, value = "The encrypted key must not be null")
   String encryptedKeyMustNotBeNull(); 
   
   @Message(id = BASE + 75, value = "EncryptionMethod was null")
   String encryptionMethodWasNull();

   @Message(id = BASE + 80, value = "Illegal base64url string!")
   String illegalBase64UrlString();
   
   @Message(id = BASE + 85, value = "The initialization vector (IV) must not be null")
   String initializationVectorMustNotBeNull();
   
   @Message(id = BASE + 90, value = "Invalid HMAC key: %s")
   String invalidHMACkey(String message);
   
   @Message(id = BASE + 95, value = "The length of the shared symmetric key must be 128 bits (16 bytes), 256 bits (32 bytes) or 512 bites (64 bytes)")
   String lengthOfSharedSymmetricKey();
   
   @Message(id = BASE + 100, value = "MAC check failed")
   String macCheckFailed();
   
   @Message(id = BASE + 105, value = "Not a MAC Algorithm")
   String notAMACalgorithm();

   @Message(id = BASE + 110, value = "Not an RSA Algorithm")
   String notAnRSAalgorithm();
   
   @Message(id = BASE + 115, value = "Not encrypted with dir algorithm")
   String notEncryptedWithDirAlgorithm();
   
   @Message(id = BASE + 120, value = "Not encrypted with RSA algorithm")
   String notEncryptedWithRSAalgorithm();
   
   @Message(id = BASE + 125, value = "Parsing error")
   String parsingError();
   
   @Message(id = BASE + 130, value = "Unable to find MessageBodyWriter")
   String unableToFindMessageBodyWriter();
   
   @Message(id = BASE + 135, value = "Unable to find reader for content type")
   String unableToFindReaderForContentType();   
   
   @Message(id = BASE + 140, value = "Unexpected encrypted key, must be omitted")
   String unexpectedEncryptedKey();
   
   @Message(id = BASE + 145, value = "Unknown length")
   String unknownLength();
   
   @Message(id = BASE + 150, value = "Unsupported algorithm, must be \"dir\"")
   String unsupportedAlgorithm();
   
   @Message(id = BASE + 155, value = "Unsupported compression algorithm: %s")
   String unsupportedCompressionAlgorithm(CompressionAlgorithm algorithm);
   
   @Message(id = BASE + 160, value = "Unsupported encryption method, must be A128CBC_HS256, A256CBC_HS512, A128GCM or A128GCM")
   String unsupportedEncryptionMethod();
   
   @Message(id = BASE + 165, value = "Unsupported HMAC algorithm: %s")
   String unsupportedHMACalgorithm(String message);
   
   @Message(id = BASE + 170, value = "Unsupported JWE algorithm, must be RSA1_5 or RSA_OAEP")
   String unsupportedJWEalgorithm();
   
   @Message(id = BASE + 175, value = "Unsupported AES/CBC/PKCS5Padding/HMAC-SHA2 key length, must be 256 or 512 bits")
   String unsupportedKeyLength();
}
