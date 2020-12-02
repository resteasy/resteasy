package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.util.FindAnnotation;

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
   protected @Context Providers workers;

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
      Providers providers = ResteasyContext.getContextData(Providers.class);
      input.setProviders(providers);
      input.parse(entityStream);

      XopWithMultipartRelatedJAXBProvider xopWithMultipartRelatedJAXBProvider = new XopWithMultipartRelatedJAXBProvider(
              workers);
      return xopWithMultipartRelatedJAXBProvider.readFrom(type, genericType,
              annotations, mediaType, httpHeaders, entityStream, input);
   }
}
