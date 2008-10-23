package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.ext.MessageBodyWorkers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MultipartFormDataInputImpl extends MultipartInputImpl implements MultipartFormDataInput
{
   protected Map<String, InputPart> formData = new HashMap<String, InputPart>();
   protected static final Pattern DISPOSITION = Pattern.compile("form-data;.*name=\"?([^\"\\s;]*)\"?.*");

   public MultipartFormDataInputImpl(String boundary, MessageBodyWorkers workers)
   {
      super(boundary, workers);
   }

   public Map<String, InputPart> getFormData()
   {
      return formData;
   }

   public <T> T getFormDataPart(String key, Class<T> rawType, Type genericType) throws IOException
   {
      InputPart part = getFormData().get(key);
      if (part == null) return null;
      return part.getBody(rawType, genericType);
   }

   @Override
   protected void extractPart(InputStream is) throws IOException
   {
      super.extractPart(is);
      String disposition = currPart.getHeaders().getFirst("Content-Disposition");
      if (disposition == null) throw new RuntimeException("Could find no Content-Disposition header within part");
      Matcher matcher = DISPOSITION.matcher(disposition);
      if (matcher.matches())
      {
         formData.put(matcher.group(1), currPart);
      }
      else
      {
         throw new RuntimeException("Could not parse Content-Disposition for MultipartFormData: " + disposition);
      }
   }

}
