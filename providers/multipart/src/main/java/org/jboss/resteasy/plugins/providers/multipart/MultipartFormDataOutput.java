package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartFormDataOutput extends MultipartOutput
{
   protected Map<String, OutputPart> formData = new LinkedHashMap<String, OutputPart>();
   protected Map<String, List<OutputPart>> formDataMap = new HashMap<String, List<OutputPart>>();

   private void addFormDataMap(String key, OutputPart part) {
      List<OutputPart> list = getFormDataMap().get(key);
      if (list == null) {
         list = new LinkedList<OutputPart>();
         formDataMap.put(key, list);
      }
      list.add(part);
   }

   public Map<String, List<OutputPart>> getFormDataMap() {
      return formDataMap;
   }

   public OutputPart addFormData(String key, Object entity, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, mediaType);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }
   
   public OutputPart addFormData(String key, Object entity, MediaType mediaType, String filename)
   {
      OutputPart part = super.addPart(entity, mediaType, filename);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }   

   public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, type, mediaType);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }
   
   public OutputPart addFormData(String key, Object entity, GenericType<?> type, MediaType mediaType, String filename)
   {
      OutputPart part = super.addPart(entity, type, mediaType, filename);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }   

   public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType)
   {
      OutputPart part = super.addPart(entity, type, genericType, mediaType);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }
   
   public OutputPart addFormData(String key, Object entity, Class<?> type, Type genericType, MediaType mediaType, String filename)
   {
      OutputPart part = super.addPart(entity, type, genericType, mediaType, filename);
      formData.put(key, part);
      addFormDataMap(key, part);
      return part;
   }   

   public Map<String, OutputPart> getFormData()
   {
      return formData;
   }
}
