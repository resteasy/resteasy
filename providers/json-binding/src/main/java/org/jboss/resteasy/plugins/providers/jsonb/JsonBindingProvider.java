package org.jboss.resteasy.plugins.providers.jsonb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.json.bind.Jsonb;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.input.ProxyInputStream;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.jsonb.i18n.Messages;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.DelegatingOutputStream;

/**
 * Created by rsearls on 6/26/17.
 */
@Provider
@Produces({"application/json", "application/*+json", "text/json"})
@Consumes({"application/json", "application/*+json", "text/json"})
@Priority(Priorities.USER-100)
public class JsonBindingProvider extends AbstractJsonBindingProvider
      implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

   private final boolean disabled;

   public JsonBindingProvider() {
      super();
      ResteasyConfiguration context = ResteasyContext.getContextData(ResteasyConfiguration.class);
      boolean disabled = (context != null && (Boolean.parseBoolean(context.getParameter(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB))
            || Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable"))));
      ResteasyProviderFactory providerFactory = ResteasyContext.getContextData(ResteasyProviderFactory.class);
      if (context == null && providerFactory != null)
      {
         Object config = providerFactory.getProperty(ResteasyContextParameters.RESTEASY_PREFER_JACKSON_OVER_JSONB);
         if (config != null)
         {
            disabled = Boolean.parseBoolean(config.toString());
         }
      }
      this.disabled = disabled;
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
      if (disabled)
      {
         return false;
      }
      return isSupportedMediaType(mediaType);
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
         throw new ProcessingException(Messages.MESSAGES.jsonBDeserializationError(e, e.getMessage()), e);
      }
   }

   private class EmptyCheckInputStream extends ProxyInputStream
   {
      boolean read = false;
      boolean empty = false;

      EmptyCheckInputStream(final InputStream proxy)
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
      return isSupportedMediaType(mediaType);
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
         throw new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()), e);
      }
   }
}
