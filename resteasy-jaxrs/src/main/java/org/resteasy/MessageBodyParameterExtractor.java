package org.resteasy;

import org.resteasy.spi.HttpRequest;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.HttpResponseCodes;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterExtractor implements ParameterExtractor
{
   private Class type;
   private Type genericType;
   private Annotation[] annotations;
   private ResteasyProviderFactory factory;

   public MessageBodyParameterExtractor(Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.factory = factory;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   public Object extract(HttpRequest request)
   {
      try
      {
         MediaType mediaType = request.getHttpHeaders().getMediaType();
         if (mediaType == null)
         {
            System.err.println("content-type was null and expecting to extract a body");
            throw new WebApplicationException(HttpResponseCodes.SC_BAD_REQUEST);
         }
         MessageBodyReader reader = factory.createMessageBodyReader(type, genericType, annotations, mediaType);
         return reader.readFrom(type, genericType, mediaType, annotations, request.getHttpHeaders().getRequestHeaders(), request.getInputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failure extracting body", e);
      }
   }
}
