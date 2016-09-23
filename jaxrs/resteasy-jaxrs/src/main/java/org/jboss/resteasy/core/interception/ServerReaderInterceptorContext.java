package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.resteasy_jaxrs.i18n.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NoContentException;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ReaderInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerReaderInterceptorContext extends AbstractReaderInterceptorContext
{
   private HttpRequest request;

   public ServerReaderInterceptorContext(ReaderInterceptor[] interceptors, ResteasyProviderFactory providerFactory, Class type,
                                         Type genericType, Annotation[] annotations, MediaType mediaType,
                                         MultivaluedMap<String, String> headers, InputStream inputStream,
                                         HttpRequest request)
   {
      super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream);

      this.request = request;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : resolveReader .")
   protected MessageBodyReader resolveReader(MediaType mediaType)
   {
      MessageBodyReader reader =  providerFactory.getServerMessageBodyReader(type,
              genericType, annotations, mediaType);
      //logger.info("**** picked reader: " + reader.getClass().getName());

      return reader;
   }

   @Override
   protected void throwReaderNotFound()
   {
      throw new NotSupportedException(Messages.MESSAGES.couldNotFindMessageBodyReader(genericType, mediaType));
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : readFrom .")
   protected Object readFrom(MessageBodyReader reader) throws IOException
   {
      try
      {
         return super.readFrom(reader);
      }
      catch (NoContentException e)
      {
         throw new BadRequestException(e);
      }
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : getProperty .")
   public Object getProperty(String name)
   {
      return request.getAttribute(name);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : getPropertyNames .")
   public Collection<String> getPropertyNames()
   {
      ArrayList<String> names = new ArrayList<String>();
      Enumeration<String> enames = request.getAttributeNames();
      while (enames.hasMoreElements())
      {
         names.add(enames.nextElement());
      }

      return names;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : setProperty .")
   public void setProperty(String name, Object object)
   {
      if (object == null)
      {
         request.removeAttribute(name);
      }
      else
      {
         request.setAttribute(name, object);
      }
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Interceptor context : org.jboss.resteasy.core.interception.ServerReaderInterceptorContext , method call : removeProperty .")
   public void removeProperty(String name)
   {
      request.removeAttribute(name);
   }
}
