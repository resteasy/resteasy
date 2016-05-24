package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 13, 2012
 */
@Decorator
public abstract class BookReaderDecorator implements MessageBodyReader<Book>
{
   @Inject private Logger log;
   @Inject private @Delegate MessageBodyReader<Book> reader;

   @Override
   public Book readFrom(Class<Book> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookReaderDecorator.readFrom()");
      VisitList.add(VisitList.READER_DECORATOR_ENTER);
      Book b = reader.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
      VisitList.add(VisitList.READER_DECORATOR_LEAVE);
      log.info("leaving BookReaderDecorator.readFrom()");
      return b;
   }
}
