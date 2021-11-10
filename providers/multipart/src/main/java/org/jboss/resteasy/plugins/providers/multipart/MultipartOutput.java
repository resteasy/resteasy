package org.jboss.resteasy.plugins.providers.multipart;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartOutput
{

   protected List<OutputPart> parts = new ArrayList<OutputPart>();
   protected String boundary = UUID.randomUUID().toString();

   public OutputPart addPart(Object entity, MediaType mediaType)
   {
      OutputPart outputPart = new OutputPart(entity, entity.getClass(), null, mediaType);
      parts.add(outputPart);
      return outputPart;
   }

   public OutputPart addPart(Object entity, MediaType mediaType, String filename)
   {
      return addPart(entity, mediaType, filename, false);
   }

   public OutputPart addPart(Object entity, MediaType mediaType, String filename, boolean utf8Encode)
   {
      OutputPart outputPart = new OutputPart(entity, entity.getClass(), null, mediaType, filename, utf8Encode);
      parts.add(outputPart);
      return outputPart;
   }

   public OutputPart addPart(Object entity, GenericType<?> type, MediaType mediaType)
   {
      OutputPart outputPart = new OutputPart(entity, type.getRawType(), type.getType(), mediaType);
      parts.add(outputPart);
      return outputPart;
   }

   public OutputPart addPart(Object entity, GenericType<?> type, MediaType mediaType, String filename)
   {
      return addPart(entity, type, mediaType, filename, false);
   }

   public OutputPart addPart(Object entity, GenericType<?> type, MediaType mediaType, String filename, boolean utf8Encode)
   {
      OutputPart outputPart = new OutputPart(entity, type.getRawType(), type.getType(), mediaType, filename, utf8Encode);
      parts.add(outputPart);
      return outputPart;
   }

   public OutputPart addPart(Object entity, Class<?> type, Type genericType, MediaType mediaType)
   {
      OutputPart outputPart = new OutputPart(entity, type, genericType, mediaType);
      parts.add(outputPart);
      return outputPart;
   }

   public OutputPart addPart(Object entity, Class<?> type, Type genericType, MediaType mediaType, String filename)
   {
      return addPart(entity, type, genericType, mediaType, filename, false);
   }

   public OutputPart addPart(Object entity, Class<?> type, Type genericType, MediaType mediaType, String filename, boolean utf8Encode)
   {
      OutputPart outputPart = new OutputPart(entity, type, genericType, mediaType, filename, utf8Encode);
      parts.add(outputPart);
      return outputPart;
   }

   public List<OutputPart> getParts()
   {
      return parts;
   }

   public String getBoundary()
   {
      return boundary;
   }

   public void setBoundary(String boundary)
   {
      this.boundary = boundary;
   }
}
