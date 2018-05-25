package org.jboss.resteasy.jose.jwe;

import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jwe.crypto.DirectEncrypter;
import org.jboss.resteasy.jose.jwe.crypto.RSAEncrypter;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.interfaces.RSAPublicKey;

/**
 *
 * Encrypt content.  Default EncryptionMethod is A256CBC_HS512
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWEBuilder
{
   String type;
   String contentType;
   byte[] contentBytes;
   Providers providers;
   CompressionAlgorithm compressionAlgorithm;
   EncryptionMethod encryptionMethod = EncryptionMethod.A256CBC_HS512;

   public JWEBuilder()
   {
      this(ResteasyProviderFactory.getInstance());
   }

   public JWEBuilder(Providers providers)
   {
      this.providers = providers;
   }

   public JWEBuilder type(String type)
   {
      this.type = type;
      return this;
   }

   public JWEBuilder contentType(String type)
   {
      this.contentType = type;
      return this;
   }

   public JWEBuilder contentType(MediaType type)
   {
      this.contentType = type.toString();
      return this;
   }

   public EncryptionBuilder contentBytes(byte[] bytes)
   {
      this.contentBytes = bytes;
      return new EncryptionBuilder();
   }

   public EncryptionBuilder content(Object content)
   {
      contentBytes = marshalContent(content, MediaType.valueOf(contentType));
      return new EncryptionBuilder();
   }


   public EncryptionBuilder content(Object content, MediaType marshalTo)
   {
      contentBytes = marshalContent(content, marshalTo);
      return new EncryptionBuilder();
   }

   protected String encodeHeader(Algorithm alg)
   {
      StringBuilder builder = new StringBuilder("{");
      builder.append("\"alg\":\"").append(alg.toString()).append("\"");
      builder.append(",\"enc\":\"").append(encryptionMethod.toString()).append("\"");
      if (compressionAlgorithm != null)
         builder.append(",\"zip\":\"").append(compressionAlgorithm.toString()).append("\"");
      if (type != null) builder.append(",\"typ\" : \"").append(type).append("\"");
      if (contentType != null) builder.append(",\"cty\":\"").append(contentType).append("\"");
      builder.append("}");
      String json = builder.toString();
	 return Base64Url.encode(json.getBytes(StandardCharsets.UTF_8));
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   protected byte[] marshalContent(Object content, MediaType marshalTo)
   {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Class type = content.getClass();
      Type genericType = null;
      Object obj = content;
      if (content instanceof GenericEntity)
      {
         GenericEntity ge = (GenericEntity) content;
         obj = ge.getEntity();
         type = ge.getRawType();
         genericType = ge.getType();
      }
      if (genericType == null) genericType = type;

      MessageBodyWriter writer = providers.getMessageBodyWriter(type, genericType, null, marshalTo);
      if (writer == null) throw new IllegalStateException(Messages.MESSAGES.unableToFindMessageBodyWriter());
      try
      {
         writer.writeTo(obj, type, genericType, null, marshalTo, new MultivaluedHashMap<String, Object>(), baos);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
      return baos.toByteArray();
   }

   public class EncryptionBuilder
   {
      public EncryptionBuilder compressed()
      {
         compressionAlgorithm = CompressionAlgorithm.DEF;
         return this;
      }

      public EncryptionBuilder A128CBC_HS256()
      {
         encryptionMethod = EncryptionMethod.A128CBC_HS256;
         return this;
      }

      public EncryptionBuilder A256CBC_HS512()
      {
         encryptionMethod = EncryptionMethod.A256CBC_HS512;
         return this;
      }

      public EncryptionBuilder A128GCM()
      {
         encryptionMethod = EncryptionMethod.A128GCM;
         return this;
      }

      public EncryptionBuilder A256GCM()
      {
         encryptionMethod = EncryptionMethod.A256GCM;
         return this;
      }


      public String RSA1_5(RSAPublicKey publicKey)
      {
         String header = encodeHeader(Algorithm.RSA1_5);
         return RSAEncrypter.encrypt(Algorithm.RSA1_5, encryptionMethod, compressionAlgorithm, publicKey, header, contentBytes);
      }

      public String RSA_OAEP(RSAPublicKey publicKey)
      {
         String header = encodeHeader(Algorithm.RSA_OAEP);
         return RSAEncrypter.encrypt(Algorithm.RSA_OAEP, encryptionMethod, compressionAlgorithm, publicKey, header, contentBytes);
      }

      /**
       * Hashes the string into the required secret key size defined by the EncryptionMethod.
       *
       * @param secret secret key
       * @return encrypted data
       */
      public String dir(String secret)
      {
         MessageDigest digest = encryptionMethod.createSecretDigester();
         byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
         return dir(hash);
      }

      /**
       * Secret must meet the size requirements of the EncryptionMethod.
       *
       * @param secret secret key
       * @return encrypted data
       */
      public String dir(byte[] secret)
      {
          SecretKey key = new SecretKeySpec(secret, "AES");
          return dir(key);
      }

      public String dir(SecretKey key)
      {
         if (!key.getAlgorithm().equals("AES")) throw new IllegalArgumentException(Messages.MESSAGES.algorithmOfSharedSymmetricKey());
         byte[] keyBytes = key.getEncoded();

         if (keyBytes.length != 16 && keyBytes.length != 32 && keyBytes.length != 64) {

            throw new IllegalArgumentException(Messages.MESSAGES.lengthOfSharedSymmetricKey());
         }

         String header = encodeHeader(Algorithm.dir);
         return DirectEncrypter.encrypt(encryptionMethod, compressionAlgorithm, header, key, contentBytes);

      }


   }


}
