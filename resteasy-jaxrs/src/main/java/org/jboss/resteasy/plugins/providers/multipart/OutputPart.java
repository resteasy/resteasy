package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class OutputPart
{
   private MultivaluedMap<String, Object> headers = new MultivaluedMapImpl<String, Object>();
   private Object entity;
   private MediaType mediaType;

   public OutputPart(Object entity, MediaType mediaType)
   {
      this.entity = entity;
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

   public MediaType getMediaType()
   {
      return mediaType;
   }
}
