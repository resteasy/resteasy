package org.jboss.resteasy.cdi.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.Entity;
import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.cdi.util.Constants;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright May 8, 2012
 */
@Stateful
@Provider
@Consumes(Constants.MEDIA_TYPE_TEST_XML)
public class EJBBookReaderImpl implements EJBBookReader, MessageBodyReader<Book>
{
   @SuppressWarnings("rawtypes")
   static private MessageBodyReader delegate;
   @Inject private Logger log;
   static private int uses;
   
   @Entity
   @XmlRootElement(name = "nonbook")
   @XmlAccessorType(XmlAccessType.FIELD)
   public class NonBook
   {
   }
   
   static
   {
      ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
      delegate = factory.getMessageBodyReader(NonBook.class, null, null, MediaType.APPLICATION_XML_TYPE);
      System.out.println("reader delegate: " + delegate);  // Should be JAXBXmlRootElementProvider.
   }
   
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Book.class.equals(type);
   }

   @SuppressWarnings("unchecked")
   public Book readFrom(Class<Book> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      log.info("entering EJBBookReader.readFrom()");
      uses++;
      return Book.class.cast(delegate.readFrom(Book.class, genericType, annotations, mediaType, httpHeaders, entityStream));
   }
   
   @Override
   public int getUses()
   {
      return uses;
   }
   
   @Override
   public void reset()
   {
      uses = 0;
   }
}

