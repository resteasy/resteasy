package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * This provider is for reading xop packages packed as multipart/related. For
 * more information see {@link XopWithMultipartRelated}.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/related")
public class XopWithMultipartRelatedReader implements MessageBodyReader<Object>
{
   protected
   @Context
   Providers workers;

   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType)
   {
      return FindAnnotation.findAnnotation(annotations,
              XopWithMultipartRelated.class) != null
              || type.isAnnotationPresent(XopWithMultipartRelated.class);
   }

   public Object readFrom(Class<Object> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
           throws IOException, WebApplicationException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null)
         throw new IOException(Messages.MESSAGES.unableToGetBoundary());
      MultipartRelatedInputImpl input = new MultipartRelatedInputImpl(
              mediaType, workers);
      input.parse(entityStream);

      XopWithMultipartRelatedJAXBProvider xopWithMultipartRelatedJAXBProvider = new XopWithMultipartRelatedJAXBProvider(
              workers);
      return xopWithMultipartRelatedJAXBProvider.readFrom(type, genericType,
              annotations, mediaType, httpHeaders, entityStream, input);
   }
}
