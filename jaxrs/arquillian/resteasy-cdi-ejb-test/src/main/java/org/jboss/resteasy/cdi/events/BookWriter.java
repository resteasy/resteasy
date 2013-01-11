package org.jboss.resteasy.cdi.events;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
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
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class BookWriter implements MessageBodyWriter<Book>
{
   static private MessageBodyWriter<Book> delegate;
   
   @Inject @Write(context="writer") Event<String> writeEvent;
   @Inject private Logger log;
   
   static
   {
      System.out.println("In BookWriter static {}");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyWriter(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
   }
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      log.info("entering BookWriter.isWriteable()");
      boolean b = Book.class.equals(type);
      log.info("leaving BookWriter.isWriteable()");
      return b;
   }

   public long getSize(Book t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      log.info("entering BookWriter.getSize()");
      log.info("leaving BookWriter.getSize()");
      return -1;
   }

   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering BookWriter.writeTo()");
      log.info("BookWriter.writeTo() writing " + t);
      delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      log.info("BookWriter firing writeEvent");
      writeEvent.fire("writeEvent");
      log.info("leaving BookWriter.writeTo()");
   }
}

