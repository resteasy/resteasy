package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
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
@Produces("multipart/*")
public class ListMultipartWriter extends AbstractMultipartWriter implements AsyncMessageBodyWriter<List<Object>>
{
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return List.class.isAssignableFrom(type) && FindAnnotation.findAnnotation(annotations, PartType.class) != null;
   }

   public long getSize(List<Object> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(List<Object> list, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      PartType partType = FindAnnotation.findAnnotation(annotations, PartType.class);
      MediaType partMediaType = MediaType.valueOf(partType.value());

      MultipartOutput output = new MultipartOutput();
      for (Object obj : list)
      {
         output.addPart(obj, partMediaType);
      }
      write(output, mediaType, httpHeaders, entityStream);
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(List<Object> list, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream) {
       PartType partType = FindAnnotation.findAnnotation(annotations, PartType.class);
       MediaType partMediaType = MediaType.valueOf(partType.value());

       MultipartOutput output = new MultipartOutput();
       for (Object obj : list)
       {
          output.addPart(obj, partMediaType);
       }
       return asyncWrite(output, mediaType, httpHeaders, entityStream);
   }
}
