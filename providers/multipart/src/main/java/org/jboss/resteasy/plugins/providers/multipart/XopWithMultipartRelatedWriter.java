package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.ProviderHelper;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.util.FindAnnotation;

/**
 * This provider is for writing xop packages packed as multipart/related. For
 * more information see {@link XopWithMultipartRelated}.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/related")
public class XopWithMultipartRelatedWriter extends
      AbstractMultipartRelatedWriter implements AsyncMessageBodyWriter<Object>
{

   public long getSize(Object t, Class<?> type, Type genericType,
                       Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType)
   {
      return FindAnnotation.findAnnotation(annotations,
              XopWithMultipartRelated.class) != null
              || type.isAnnotationPresent(XopWithMultipartRelated.class);
   }

   public void writeTo(Object t, Class<?> type, Type genericType,
                       Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException,
         WebApplicationException
   {

      MultipartRelatedOutput xopPackage = new MultipartRelatedOutput();

      XopWithMultipartRelatedJAXBProvider xopWithMultipartRelatedJAXBProvider = new XopWithMultipartRelatedJAXBProvider(
              workers);
      xopWithMultipartRelatedJAXBProvider.writeTo(t, type, genericType,
              annotations, mediaType, httpHeaders, xopPackage);
      writeRelated(xopPackage, mediaType, httpHeaders, entityStream);
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                                             MultivaluedMap<String, Object> httpHeaders, AsyncOutputStream entityStream) {
       MultipartRelatedOutput xopPackage = new MultipartRelatedOutput();

       XopWithMultipartRelatedJAXBProvider xopWithMultipartRelatedJAXBProvider = new XopWithMultipartRelatedJAXBProvider(workers);
       try {
           xopWithMultipartRelatedJAXBProvider.writeTo(t, type, genericType,
                                                       annotations, mediaType, httpHeaders, xopPackage);
       } catch (IOException e) {
           return ProviderHelper.completedException(e);
       }
       return asyncWriteRelated(xopPackage, mediaType, httpHeaders, entityStream);
   }
}
