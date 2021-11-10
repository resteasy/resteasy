package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/form-data")
public class MapMultipartFormDataWriter extends AbstractMultipartFormDataWriter implements AsyncMessageBodyWriter<Map<String, Object>>
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

   @Override
   public CompletionStage<Void> asyncWriteTo(Map<String, Object> map, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream) {
       PartType partType = FindAnnotation.findAnnotation(annotations, PartType.class);
       MediaType partMediaType = MediaType.valueOf(partType.value());

       MultipartFormDataOutput output = new MultipartFormDataOutput();
       for (Map.Entry<String, Object> entry : map.entrySet())
       {
          output.addFormData(entry.getKey(), entry.getValue(), partMediaType);
       }
       return asyncWrite(output, mediaType, httpHeaders, entityStream);
   }
}
