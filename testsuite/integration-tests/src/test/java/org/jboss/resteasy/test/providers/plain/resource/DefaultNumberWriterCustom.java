package org.jboss.resteasy.test.providers.plain.resource;

import org.jboss.logging.Logger;
import org.jboss.resteasy.plugins.providers.DefaultNumberWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.CompletionStage;

@Provider
public class DefaultNumberWriterCustom extends DefaultNumberWriter {
   public static volatile boolean used;
   private static Logger logger = Logger.getLogger(DefaultNumberWriterCustom.class);

   @Override
   public void writeTo(Number n, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
      logger.info("DefaultNumberWriterCustom.writeTo()");
      used = true;
      super.writeTo(n, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }

   @Override
   public CompletionStage<Void> asyncWriteTo(Number n, Class<?> type, Type genericType, Annotation[] annotations,
                                             MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                                             AsyncOutputStream entityStream)
   {
      logger.info("DefaultNumberWriterCustom.asyncWriteTo()");
      used = true;
      return super.asyncWriteTo(n, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }
}
