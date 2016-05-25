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
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/*")
public class MultipartReader implements MessageBodyReader<MultipartInput>
{
   protected
   @Context
   Providers workers;


   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(MultipartInput.class);
   }

   public MultipartInput readFrom(Class<MultipartInput> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null) throw new IOException(Messages.MESSAGES.unableToGetBoundary());
      MultipartInputImpl input = new MultipartInputImpl(mediaType, workers);
      /*
      StringWriter writer = new StringWriter();
      int b;
      do
      {
         b = entityStream.read();
         if (b == -1) break;
         writer.write(b);
      } while (b != -1);
      System.out.println(writer.toString());
      */
      input.parse(entityStream);
      return input;
   }
}
