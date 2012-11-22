package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 14, 2012
 */
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class BookReader implements MessageBodyReader<Book>
{
   static private MessageBodyReader<Book> delegate;
   
   @Inject private Logger log;
   
   static
   {
      System.out.println("In BookReader static {}");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyReader(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
      System.out.println("In BookReader static {}");
   }
   
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      System.out.println("entering BookReader.isReadable()");
      boolean b = Book.class.equals(type);
      System.out.println("leaving BookReader.isReadable()");
      return b;
   }

   public Book readFrom(Class<Book> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookReader.readFrom()");
      Book book = Book.class.cast(delegate.readFrom(Book.class, genericType, annotations, mediaType, httpHeaders, entityStream));
      log.info("BookReader.readFrom() read " + book);
      log.info("leaving BookReader.readFrom()");
      return book;
   }
}

