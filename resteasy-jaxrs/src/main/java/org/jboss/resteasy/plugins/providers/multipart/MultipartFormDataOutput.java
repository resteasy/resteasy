package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.MediaType;
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

   public Map<String, OutputPart> getFormData()
   {
      return formData;
   }
}
