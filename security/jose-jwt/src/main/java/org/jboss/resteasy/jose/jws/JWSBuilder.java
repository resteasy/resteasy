package org.jboss.resteasy.jose.jws;

import org.jboss.resteasy.jose.Base64Url;
import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jws.crypto.HMACProvider;
import org.jboss.resteasy.jose.jws.crypto.RSAProvider;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.crypto.SecretKey;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
   String contentType;
   Object content;
   byte[] contentBytes;
   MediaType marshalTo;
   Providers providers;

   public JWSBuilder()
   {
      this(ResteasyProviderFactory.getInstance());
   }

   public JWSBuilder(Providers providers)
   {
      this.providers = providers;
   }

   public JWSBuilder type(String type)
   {
      this.type = type;
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

   protected String encodeHeader(Algorithm alg)
   {
      StringBuilder builder = new StringBuilder("{");
      builder.append("\"alg\":\"").append(alg.toString()).append("\"");

      if (type != null) builder.append(",\"typ\" : \"").append(type).append("\"");
      if (contentType != null) builder.append(",\"cty\":\"").append(contentType).append("\"");
      builder.append("}");
      return Base64Url.encode(builder.toString().getBytes(StandardCharsets.UTF_8));
   }

   protected String encode(Algorithm alg, byte[] data, byte[] signature)
   {
      StringBuffer encoding = new StringBuffer();
      encoding.append(encodeHeader(alg));
      encoding.append('.');
      encoding.append(Base64Url.encode(data));
      encoding.append('.');
      if (alg != Algorithm.none)
      {
         encoding.append(Base64Url.encode(signature));
      }
      return encoding.toString();
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
      public String none()
      {
         byte[] data = marshalContent();
         return encode(Algorithm.none, data, null);
      }
      public String rsa256(PrivateKey privateKey)
      {
         byte[] data = marshalContent();
         byte[] signature = RSAProvider.sign(data, Algorithm.RS256, privateKey);
         return encode(Algorithm.RS256, data, signature);
      }
      public String rsa384(PrivateKey privateKey)
      {
         byte[] data = marshalContent();
         byte[] signature = RSAProvider.sign(data, Algorithm.RS384, privateKey);
         return encode(Algorithm.RS384, data, signature);
      }
      public String rsa512(PrivateKey privateKey)
      {
         byte[] data = marshalContent();
         byte[] signature = RSAProvider.sign(data, Algorithm.RS512, privateKey);
         return encode(Algorithm.RS512, data, signature);
      }


      public String hmac256(byte[] sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS256, sharedSecret);
         return encode(Algorithm.HS256, data, signature);
      }

      public String hmac384(byte[] sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS384, sharedSecret);
         return encode(Algorithm.HS384, data, signature);
      }

      public String hmac512(byte[] sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS512, sharedSecret);
         return encode(Algorithm.HS512, data, signature);
      }

      public String hmac256(SecretKey sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS256, sharedSecret);
         return encode(Algorithm.HS256, data, signature);
      }

      public String hmac384(SecretKey sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS384, sharedSecret);
         return encode(Algorithm.HS384, data, signature);
      }

      public String hmac512(SecretKey sharedSecret)
      {
         byte[] data = marshalContent();
         byte[] signature = HMACProvider.sign(data, Algorithm.HS512, sharedSecret);
         return encode(Algorithm.HS512, data, signature);
      }





   }


}
