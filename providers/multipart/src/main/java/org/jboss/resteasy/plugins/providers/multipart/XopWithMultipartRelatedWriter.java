package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
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
        AbstractMultipartRelatedWriter implements MessageBodyWriter<Object>
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

}
