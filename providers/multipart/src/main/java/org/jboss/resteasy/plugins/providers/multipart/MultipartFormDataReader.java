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
@Consumes("multipart/form-data")
public class MultipartFormDataReader implements MessageBodyReader<MultipartFormDataInput>
{
   protected
   @Context
   Providers workers;


   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.equals(MultipartFormDataInput.class);
   }

   public MultipartFormDataInput readFrom(Class<MultipartFormDataInput> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      /*
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int b;
      do
      {
         b = entityStream.read();
         if (b == -1) break;
         baos.write(b);
      } while (b != -1);

      System.out.println(new String(baos.toByteArray()));
      */

      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null) throw new IOException(Messages.MESSAGES.unableToGetBoundary());
      MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(mediaType, workers);
      input.parse(entityStream);
      return input;
   }
}