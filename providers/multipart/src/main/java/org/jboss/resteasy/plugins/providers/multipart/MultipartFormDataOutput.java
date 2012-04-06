package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartFormDataOutput extends MultipartOutput
{
   protected Map<String, OutputPart> formData = new LinkedHashMap<String, OutputPart>();

   public OutputPart addFormData(String key, Object entity, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, mediaType);
      formData.put(key, part);
      return part;
   }

   public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, type, mediaType);
      formData.put(key, part);
      return part;
   }

   public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, type, genericType, mediaType);
      formData.put(key, part);
      return part;
   }

   public Map<String, OutputPart> getFormData()
   {
      return formData;
   }
}
