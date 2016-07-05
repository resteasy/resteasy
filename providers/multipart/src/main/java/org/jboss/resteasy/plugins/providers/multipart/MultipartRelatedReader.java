package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * The {@link MessageBodyReader} implementation to deserialize
 * {@link MultipartRelatedInput} objects.
 *
 * @author Attila Kiraly
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/related")
public class MultipartRelatedReader implements
        MessageBodyReader<MultipartRelatedInput>
{
   protected
   @Context
   Providers workers;

   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(MultipartRelatedInput.class);
   }

   public MultipartRelatedInput readFrom(Class<MultipartRelatedInput> type,
                                         Type genericType, Annotation[] annotations, MediaType mediaType,
                                         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
           throws IOException, WebApplicationException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null)
         throw new IOException(Messages.MESSAGES.unableToGetBoundary());
      MultipartRelatedInputImpl input = new MultipartRelatedInputImpl(
              mediaType, workers);
      input.parse(entityStream);
      return input;
   }
}
