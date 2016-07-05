package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
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
@Produces(Constants.MEDIA_TYPE_TEST_XML)
public class BookWriter implements MessageBodyWriter<Book>
{
   static private MessageBodyWriter<Book> delegate;
   
   static
   {
      System.out.println("In BookWriter static {}");
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyWriter(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
   }
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      System.out.println("entering BookWriter.isWriteable()");
      boolean b = Book.class.equals(type);
      System.out.println("leaving BookWriter.isWriteable()");
      return b;
   }

   public long getSize(Book t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      System.out.println("entering BookWriter.getSize()");
      System.out.println("leaving BookWriter.getSize()");
      return -1;
   }

   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      System.out.println("entering BookWriter.writeTo()");
      System.out.println("BookWriter.writeTo() writing " + t);
      delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
      System.out.println("leaving BookWriter.writeTo()");
   }
}

