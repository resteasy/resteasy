package org.jboss.resteasy.plugins.providers.jsonb;

import org.jboss.resteasy.plugins.providers.jsonb.i18n.Messages;

import javax.json.bind.Jsonb;
import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.plugins.providers.jsonb.i18n.LogMessages;

/**
 * Created by rsearls on 6/26/17.
 */
@Provider
@Produces({"application/json", "application/*+json", "text/json", "*/*"})
@Consumes({"application/json", "application/*+json", "text/json", "*/*"})
@Priority(Priorities.USER-100)
public class JsonBindingProvider extends AbstractJsonBindingProvider
        implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
      return (isSupportedMediaType(mediaType))
         && ((!type.isAnnotationPresent(XmlRootElement.class) && !type.isAnnotationPresent(XmlType.class))
         || (FindAnnotation.findJsonBindingAnnotations(annotations).length != 0));
   }

   @Override
   public Object readFrom(Class<Object> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders,
                                 InputStream entityStream) throws java.io.IOException, javax.ws.rs.WebApplicationException {
      Jsonb jsonb = getJsonb(type);
      try
      {
         return jsonb.fromJson(entityStream, genericType);
      } catch (Throwable e)
      {
         if (LogMessages.LOGGER.isDebugEnabled()) {
            LogMessages.LOGGER.debugf(Messages.MESSAGES.jsonBDeserializationError(e.toString()));
         }
 
         // detail text provided in logger message
         throw new ProcessingException(Messages.MESSAGES.jsonBDeserializationError(""));
      }
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      return (isSupportedMediaType(mediaType))
         && ((!type.isAnnotationPresent(XmlRootElement.class) && !type.isAnnotationPresent(XmlType.class))
              || (FindAnnotation.findJsonBindingAnnotations(annotations).length != 0));
   }

   @Override
   public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations,
                       MediaType mediaType) {
      return -1L;
   }

   @Override
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream)
           throws java.io.IOException, javax.ws.rs.WebApplicationException {
      Jsonb jsonb = getJsonb(type);
      try
      {
         entityStream.write(jsonb.toJson(t).getBytes(getCharset(mediaType)));
         entityStream.flush();
      } catch (Throwable e)
      {
         throw new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()));
      }
   }
}
