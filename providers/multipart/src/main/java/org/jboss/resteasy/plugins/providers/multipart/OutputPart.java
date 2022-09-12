package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OutputPart
{
   private MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
   private Object entity;
   private Class<?> type;
   private Type genericType;
   private MediaType mediaType;
   private String filename;
   private boolean utf8Encode;

   public OutputPart(final Object entity, final Class<?> type, final Type genericType, final MediaType mediaType)
   {
      this(entity, type, genericType, mediaType, null);
   }

   public OutputPart(final Object entity, final Class<?> type, final Type genericType, final MediaType mediaType, final String filename)
   {
      this(entity, type, genericType, mediaType, null, false);
   }

   public OutputPart(final Object entity, final Class<?> type, final Type genericType, final MediaType mediaType, final String filename, final boolean utf8Encode)
   {
      this.entity = entity;
      this.type = type;
      this.genericType = genericType;
      this.mediaType = mediaType;
      this.filename = filename;
      this.utf8Encode = utf8Encode;
   }

   public MultivaluedMap<String, Object> getHeaders()
   {
      return headers;
   }

   public Object getEntity()
   {
      return entity;
   }

   public Class<?> getType()
   {
      return type;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public String getFilename()
   {
      return filename;
   }

   public boolean isUtf8Encode()
   {
      return utf8Encode;
   }
}
