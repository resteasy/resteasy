package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterInjector implements ValueInjector
{
   private Class type;
   private Type genericType;
   private Annotation[] annotations;
   private ResteasyProviderFactory factory;

   public MessageBodyParameterInjector(Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.factory = factory;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      try
      {
         MediaType mediaType = request.getHttpHeaders().getMediaType();
         if (mediaType == null)
         {
            throw new Failure("content-type was null and expecting to extract a body", HttpResponseCodes.SC_BAD_REQUEST);
         }
         MessageBodyReader reader = factory.createMessageBodyReader(type, genericType, annotations, mediaType);
         if (reader == null)
            throw new Failure("Could not find message body reader for type: " + genericType + " of content type: " + mediaType, HttpResponseCodes.SC_BAD_REQUEST);
         return reader.readFrom(type, genericType, annotations, mediaType, request.getHttpHeaders().getRequestHeaders(), request.getInputStream());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Failure extracting body", e);
      }
   }

   public Object inject()
   {
      throw new RuntimeException("Illegal to inject a message body into a singleton");
   }
}
