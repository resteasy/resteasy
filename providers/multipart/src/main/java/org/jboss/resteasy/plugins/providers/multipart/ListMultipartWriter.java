package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/*")
public class ListMultipartWriter extends AbstractMultipartWriter implements MessageBodyWriter<List<Object>>
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
}
