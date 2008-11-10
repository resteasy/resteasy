package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OutputPart
{
   private MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
   private Object entity;
   private Class type;
   private Type genericType;
   private MediaType mediaType;

   public OutputPart(Object entity, Class type, Type genericType, MediaType mediaType)
   {
      this.entity = entity;
      this.type = type;
      this.genericType = genericType;
      this.mediaType = mediaType;
   }

   public MultivaluedMap<String, Object> getHeaders()
   {
      return headers;
   }

   public Object getEntity()
   {
      return entity;
   }

   public Class getType()
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
}
