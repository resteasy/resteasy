package org.jboss.resteasy.jose.jws;

import org.jboss.resteasy.crypto.SignatureSignerContext;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jws.crypto.HMACProvider;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.jose.jws.util.Base64Url;
import org.jboss.resteasy.jose.jws.util.JsonSerialization;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.crypto.SecretKey;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Providers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWSBuilder
{
   String type;
   String kid;
   String contentType;
   Object content;
   byte[] contentBytes;
   MediaType marshalTo;
   Providers providers;

   public JWSBuilder()
   {
      this(ResteasyProviderFactory.getInstance());
   }

   public JWSBuilder(final Providers providers)
   {
      this.providers = providers;
   }

   public JWSBuilder type(String type)
   {
      this.type = type;
      return this;
   }

   public JWSBuilder kid(String kid) {
      this.kid = kid;
      return this;
   }

   public JWSBuilder contentType(String type)
   {
      this.contentType = type;
      return this;
   }

   public JWSBuilder contentType(MediaType type)
   {
      this.contentType = type.toString();
      return this;
   }

   public EncodingBuilder content(byte[] bytes)
   {
      this.contentBytes = bytes;
      return new EncodingBuilder();
   }

   public EncodingBuilder content(Object object, MediaType marshalTo)
   {
      this.content = object;
      this.marshalTo = marshalTo;
      return new EncodingBuilder();
   }

   public EncodingBuilder jsonContent(Object object) {
      try {
         this.contentBytes = JsonSerialization.writeValueAsBytes(object);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return new EncodingBuilder();
   }

protected String encodeHeader(String sigAlgName) {
   StringBuilder builder = new StringBuilder("{");
   builder.append("\"alg\":\"").append(sigAlgName).append("\"");

   if (type != null) {
      builder.append(",\"typ\" : \"").append(type).append("\"");
   }
   if (kid != null) {
      builder.append(",\"kid\" : \"").append(kid).append("\"");
   }
   if (contentType != null) {
      builder.append(",\"cty\":\"").append(contentType).append("\"");
   }
   builder.append("}");
   return Base64Url.encode(builder.toString().getBytes(StandardCharsets.UTF_8));
}
   protected String encodeAll(StringBuilder encoding, byte[] signature) {
      encoding.append('.');
      if (signature != null) {
         encoding.append(Base64Url.encode(signature));
      }
      return encoding.toString();
   }

   protected void encode(Algorithm alg, byte[] data, StringBuilder encoding) {
      encode(alg.name(), data, encoding);
   }

   protected void encode(String sigAlgName, byte[] data, StringBuilder encoding) {
      encoding.append(encodeHeader(sigAlgName));
      encoding.append('.');
      encoding.append(Base64Url.encode(data));
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   protected byte[] marshalContent()
   {
      if (contentBytes != null) return contentBytes;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      Class<?> type = content.getClass();
      Type genericType = null;
      Object obj = content;
      if (content instanceof GenericEntity)
      {
         GenericEntity<?> ge = (GenericEntity<?>)content;
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

   public class EncodingBuilder
   {
      public String sign(SignatureSignerContext signer) {
         kid = signer.getKid();

         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(signer.getAlgorithm(), data, buffer);
         byte[] signature = null;
         try {
            signature = signer.sign(buffer.toString().getBytes(StandardCharsets.UTF_8));
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
         return encodeAll(buffer, signature);
      }

      public String none() {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.none, data, buffer);
         return encodeAll(buffer, null);
      }

      public String sign(Algorithm algorithm, PrivateKey privateKey) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(algorithm, data, buffer);
         byte[] signature = RSAProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), algorithm, privateKey);
         return encodeAll(buffer, signature);
      }

      public String rsa256(PrivateKey privateKey) {
         return sign(Algorithm.RS256, privateKey);
      }

      public String rsa384(PrivateKey privateKey) {
         return sign(Algorithm.RS384, privateKey);
      }

      public String rsa512(PrivateKey privateKey) {
         return sign(Algorithm.RS512, privateKey);
      }

      public String hmac256(byte[] sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS256, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS256, sharedSecret);
         return encodeAll(buffer, signature);
      }

      public String hmac384(byte[] sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS384, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS384, sharedSecret);
         return encodeAll(buffer, signature);
      }

      public String hmac512(byte[] sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS512, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS512, sharedSecret);
         return encodeAll(buffer, signature);
      }

      public String hmac256(SecretKey sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS256, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS256, sharedSecret);
         return encodeAll(buffer, signature);
      }

      public String hmac384(SecretKey sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS384, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS384, sharedSecret);
         return encodeAll(buffer, signature);
      }

      public String hmac512(SecretKey sharedSecret) {
         StringBuilder buffer = new StringBuilder();
         byte[] data = marshalContent();
         encode(Algorithm.HS512, data, buffer);
         byte[] signature = HMACProvider.sign(buffer.toString().getBytes(StandardCharsets.UTF_8), Algorithm.HS512, sharedSecret);
         return encodeAll(buffer, signature);
      }

   }

}
