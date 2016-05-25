package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class BookWriterDecorator implements MessageBodyWriter<Book>
{
   @Inject private Logger log;
   @Inject private @Delegate MessageBodyWriter<Book> writer;

   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookWriterDecorator.writeTo()");
      VisitList.add(VisitList.WRITER_DECORATOR_ENTER);
      writer.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      VisitList.add(VisitList.WRITER_DECORATOR_LEAVE);
      log.info("leaving BookWriterDecorator.writeTo()");
   }
}
