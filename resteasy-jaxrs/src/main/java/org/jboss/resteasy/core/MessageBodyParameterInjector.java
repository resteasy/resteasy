package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.Encoded;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
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

   public boolean isFormData(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (!mediaType.equals(MediaType.APPLICATION_FORM_URLENCODED_TYPE)) return false;
      if (!MultivaluedMap.class.isAssignableFrom(type)) return false;
      if (genericType == null) return true;

      if (!(genericType instanceof ParameterizedType)) return false;
      ParameterizedType params = (ParameterizedType) genericType;
      if (params.getActualTypeArguments().length != 2) return false;
      return params.getActualTypeArguments()[0].equals(String.class) && params.getActualTypeArguments()[1].equals(String.class);
   }


   public Object inject(HttpRequest request, HttpResponse response)
   {
      try
      {
         MediaType mediaType = request.getHttpHeaders().getMediaType();
         if (mediaType == null)
         {
            throw new LoggableFailure("content-type was null and expecting to extract a body", HttpResponseCodes.SC_BAD_REQUEST);
         }

         // We have to do this hack because of servlets and servlet filters
         // A filter that does getParameter() will screw up the input stream which will screw up the
         // provider.  We do it here rather than hack the provider as the provider is reused for client side
         // and also, the server may be using the client framework to make another remote call.
         if (isFormData(type, genericType, annotations, mediaType))
         {
            boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
            if (encoded) return request.getFormParameters();
            else return request.getDecodedFormParameters();
         }
         else
         {
            MessageBodyReader reader = factory.getMessageBodyReader(type, genericType, annotations, mediaType);
            if (reader == null)
               throw new LoggableFailure("Could not find message body reader for type: " + genericType + " of content type: " + mediaType, HttpResponseCodes.SC_BAD_REQUEST);
            return reader.readFrom(type, genericType, annotations, mediaType, request.getHttpHeaders().getRequestHeaders(), request.getInputStream());
         }
      }
      catch (IOException e)
      {
         throw new LoggableFailure("Failure extracting body", e, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);
      }
   }

   public Object inject()
   {
      throw new RuntimeException("Illegal to inject a message body into a singleton");
   }
}
