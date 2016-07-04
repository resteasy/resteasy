package org.jboss.resteasy.plugins.providers.multipart;

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

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/form-data")
public class MultipartFormDataWriter extends AbstractMultipartFormDataWriter implements MessageBodyWriter<MultipartFormDataOutput>
{
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return MultipartFormDataOutput.class.isAssignableFrom(type);
   }

   public long getSize(MultipartFormDataOutput multipartFormDataOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(MultipartFormDataOutput multipartFormDataOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      write(multipartFormDataOutput, mediaType, httpHeaders, entityStream);
   }

}
