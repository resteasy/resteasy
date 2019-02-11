package org.jboss.resteasy.plugins.providers.jsonb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Priority;
import javax.json.bind.Jsonb;
import javax.validation.constraints.Null;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.io.input.ProxyInputStream;
import org.jboss.resteasy.plugins.providers.jsonb.i18n.Messages;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;
import org.jboss.resteasy.util.DelegatingOutputStream;

/**
 * Created by rsearls on 6/26/17.
 */
@Provider
@Produces({"application/json", "application/*+json", "text/json", "*/*"})
@Consumes({"application/json", "application/*+json", "text/json", "*/*"})
@Priority(Priorities.USER-100)
public class JsonBindingProvider extends AbstractJsonBindingProvider
        implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

   private final boolean disabled;
   
   public JsonBindingProvider() {
      super();
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      disabled = (context != null && (Boolean.parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB))
                || Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable"))));
   }
   
   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
      if (disabled)
      {
         return false;
      }
      if (isGenericJaxb(type, genericType))
      {
         return false;
      }
      return (isSupportedMediaType(mediaType))
    		  && ((FindAnnotation.hasJsonBindingAnnotations(annotations)) || (!isJaxbClass(type)));
   }

   @Override
   public Object readFrom(Class<Object> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders,
                                 InputStream entityStream) throws java.io.IOException, javax.ws.rs.WebApplicationException {
      Jsonb jsonb = getJsonb(type);
      final EmptyCheckInputStream is = new EmptyCheckInputStream(entityStream);
      
      try {
         return jsonb.fromJson(is, genericType);
         // If null is returned, considered to be empty stream
      } catch (Throwable e)
      {
         if (is.isEmpty()) {
            return null;
         }
         // detail text provided in logger message
         throw new ProcessingException(Messages.MESSAGES.jsonBDeserializationError(e));
      }
   }
   
   private class EmptyCheckInputStream extends ProxyInputStream
   {
      boolean read = false;
      boolean empty = false;
      
      public EmptyCheckInputStream(InputStream proxy)
      {
         super(proxy);
      }

      @Override
      protected synchronized void afterRead(final int n) throws IOException {
         if (!read && n <= 0) {
            empty = true;
         }
         read = true;
      }
      
      public boolean isEmpty() {
         return empty;
      }
   };

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      if (disabled)
      {
         return false;
      }
      if (isGenericJaxb(type, genericType))
      {
         return false;
      }
      return (isSupportedMediaType(mediaType))
            && ((FindAnnotation.hasJsonBindingAnnotations(annotations)) || (!isJaxbClass(type)));
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
         entityStream = new DelegatingOutputStream(entityStream) {
            @Override
            public void flush() throws IOException {
               // don't flush as this is a performance hit on Undertow.
               // and causes chunked encoding to happen.
            }
         };
         entityStream.write(jsonb.toJson(t).getBytes(getCharset(mediaType)));
         entityStream.flush();
      } catch (Throwable e)
      {
         throw new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()));
      }
   }
   
   private boolean isGenericJaxb(Class<?> type, Type genericType)
   {
      if (Map.class.isAssignableFrom(type) && genericType != null)
      {
         Class<?> valueType = Types.getMapValueType(genericType);
         if (valueType != null && isJaxbClass(valueType))
         {
            return true;
         }
      }

      if ((Collection.class.isAssignableFrom(type) || type.isArray()) && genericType != null)
      {
         Class<?> baseType = Types.getCollectionBaseType(type, genericType);
         if (baseType != null && isJaxbClass(baseType))
         {
            return true;
         }
      }
      return false;
   }

   private boolean isJaxbClass(Class<?> classType)
   {
      if (JAXBElement.class.equals(classType))
      {
         return true;
      }
      for (Annotation a : classType.getAnnotations()) {
         Class<? extends Annotation> c = a.annotationType();
         if (c.equals(XmlRootElement.class) || c.equals(XmlType.class) ||c.equals(XmlJavaTypeAdapter.class) ||c.equals(XmlSeeAlso.class))
         {
            return true;
         }
      }
      return false;

   }
}
