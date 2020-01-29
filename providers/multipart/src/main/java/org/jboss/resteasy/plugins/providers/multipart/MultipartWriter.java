package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.interception.jaxrs.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/*")
public class MultipartWriter extends AbstractMultipartWriter implements AsyncMessageBodyWriter<MultipartOutput>
{


   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return MultipartOutput.class.isAssignableFrom(type);
   }

   public long getSize(MultipartOutput multipartOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }


   public void writeTo(MultipartOutput multipartOutput, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      write(multipartOutput, mediaType, httpHeaders, entityStream);
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(MultipartOutput multipartOutput, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream) {
       return asyncWrite(multipartOutput, mediaType, httpHeaders, entityStream);
   }
}
