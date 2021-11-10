package org.jboss.resteasy.jose.jws;

import org.jboss.resteasy.jose.i18n.Messages;
import org.jboss.resteasy.jose.jws.util.Base64Url;
import org.jboss.resteasy.jose.jws.util.JsonSerialization;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWSInput
{
   String wireString;
   String encodedHeader;
   String encodedContent;
   String encodedSignature;
   String encodedSignatureInput;
   JWSHeader header;
   Providers providers;
   byte[] content;
   byte[] signature;

public JWSInput(final String wire, final Providers providers) throws JWSInputException {
   try {
      this.providers = providers;
      this.wireString = wire;
      String[] parts = wire.split("\\.");
      if (parts.length < 2 || parts.length > 3) throw new IllegalArgumentException("Parsing error");
      encodedHeader = parts[0];
      encodedContent = parts[1];
      encodedSignatureInput = encodedHeader + '.' + encodedContent;
      content = Base64Url.decode(encodedContent);
      if (parts.length > 2) {
         encodedSignature = parts[2];
         signature = Base64Url.decode(encodedSignature);

      }
      byte[] headerBytes = Base64Url.decode(encodedHeader);
      header = JsonSerialization.readValue(headerBytes, JWSHeader.class);
   } catch (Throwable t) {
      throw new JWSInputException(t);
   }
}

   public String getWireString()
   {
      return wireString;
   }

   public String getEncodedHeader()
   {
      return encodedHeader;
   }

   public String getEncodedContent()
   {
      return encodedContent;
   }

   public String getEncodedSignature()
   {
      return encodedSignature;
   }
   public String getEncodedSignatureInput() {
      return encodedSignatureInput;
   }

   public JWSHeader getHeader()
   {
      return header;
   }

   public byte[] getContent()
   {
      return content;
   }

   public byte[] getSignature()
   {
      return signature;
   }

   public boolean verify(String key) {
      if (header.getAlgorithm().getProvider() == null) {
         throw new RuntimeException("signing algorithm not supported");
      }
      return header.getAlgorithm().getProvider().verify(this, key);
   }

   public <T> T readJsonContent(Class<T> type) throws JWSInputException {
      try {
         return JsonSerialization.readValue(content, type);
      } catch (IOException e) {
         throw new JWSInputException(e);
      }
   }

   public String readContentAsString() {
      return new String(content, StandardCharsets.UTF_8);
   }

   @SuppressWarnings("unchecked")
   public <T> T readContent(Class<T> type)
   {
      MediaType mediaType = MediaType.WILDCARD_TYPE;
      if (header.getContentType() != null)
      {
         mediaType = MediaType.valueOf(header.getContentType());
      }
      return (T) readContent(type, null, null, mediaType);

   }

   @SuppressWarnings("unchecked")
   public Object readContent(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      @SuppressWarnings("rawtypes")
      MessageBodyReader reader = providers.getMessageBodyReader(type, genericType, annotations, mediaType);
      if (reader == null) throw new RuntimeException(Messages.MESSAGES.unableToFindReaderForContentType());

      try
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(content);
         return reader.readFrom(type, genericType, annotations, mediaType, new MultivaluedHashMap<String, String>(), bais);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
}
