package org.jboss.resteasy.cdi.injection;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
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
@ApplicationScoped
public class BookWriter implements MessageBodyWriter<Book>
{
   static private MessageBodyWriter<Book> delegate;

   @Inject private DependentScoped dependent;
   @Inject private StatefulEJB stateful;
   
   static
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyWriter(Book.class, null, null, Constants.MEDIA_TYPE_TEST_XML_TYPE);
   }
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Book.class.equals(type);
   }

   public long getSize(Book t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(Book t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      delegate.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
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

