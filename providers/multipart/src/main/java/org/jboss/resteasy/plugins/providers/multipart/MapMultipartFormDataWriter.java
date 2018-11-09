package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/form-data")
public class MapMultipartFormDataWriter extends AbstractMultipartFormDataWriter implements MessageBodyWriter<Map<String, Object>>
{
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Map.class.isAssignableFrom(type) && FindAnnotation.findAnnotation(annotations, PartType.class) != null;
   }

   public long getSize(Map<String, Object> map, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Map<String, Object> map, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      PartType partType = FindAnnotation.findAnnotation(annotations, PartType.class);
      MediaType partMediaType = MediaType.valueOf(partType.value());

      MultipartFormDataOutput output = new MultipartFormDataOutput();
      for (Map.Entry<String, Object> entry : map.entrySet())
      {
         output.addFormData(entry.getKey(), entry.getValue(), partMediaType);
      }
      write(output, mediaType, httpHeaders, entityStream);
   }
}
