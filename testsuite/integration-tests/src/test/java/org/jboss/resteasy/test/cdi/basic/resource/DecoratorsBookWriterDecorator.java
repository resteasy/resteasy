package org.jboss.resteasy.test.cdi.basic.resource;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

@Decorator
public abstract class DecoratorsBookWriterDecorator implements MessageBodyWriter<EJBBook> {
   @Inject
   private Logger log;

   @Inject
   @Delegate
   private MessageBodyWriter<EJBBook> writer;

   @Override
   public void writeTo(EJBBook t, Class<?> type, Type genericType,
                  Annotation[] annotations, MediaType mediaType,
                  MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException {
      log.info("entering DecoratorsBookWriterDecorator.writeTo()");
      DecoratorsVisitList.add(DecoratorsVisitList.WRITER_DECORATOR_ENTER);
      writer.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      DecoratorsVisitList.add(DecoratorsVisitList.WRITER_DECORATOR_LEAVE);
      log.info("leaving DecoratorsBookWriterDecorator.writeTo()");
   }
}
