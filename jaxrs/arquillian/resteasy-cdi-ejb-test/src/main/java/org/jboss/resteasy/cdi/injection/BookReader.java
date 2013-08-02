package org.jboss.resteasy.cdi.injection;

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
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class BookReader implements MessageBodyReader<Book>
{
   static private MessageBodyReader<Book> delegate;
   
   @Inject private Logger log;
   @Inject private DependentScoped dependent;
   @Inject private StatefulEJB stateful;
   
   static
   {
      System.out.println("In BookReader static {}");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyReader(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
      System.out.println("In BookReader static {}");
   }
   public BookReader()
   {
      System.out.println("entered BookReader()");
   }
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Book.class.equals(type);
   }

   public Book readFrom(Class<Book> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookReader.readFrom()");
      return Book.class.cast(delegate.readFrom(Book.class, genericType, annotations, mediaType, httpHeaders, entityStream));
   }

   public DependentScoped getDependent()
   {
      return dependent;
   }

   public StatefulEJB getStateful()
   {
      return stateful;
   }
}

