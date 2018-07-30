package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

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
      super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream, RESTEasyTracingLogger.getInstance(request));
      this.request = request;
   }

   @Override
   protected MessageBodyReader resolveReader(MediaType mediaType)
   {
      @SuppressWarnings(value = "unchecked")
      MessageBodyReader reader =  providerFactory.getServerMessageBodyReader(type,
              genericType, annotations, mediaType, tracingLogger);
      //logger.info("**** picked reader: " + reader.getClass().getName());
      return reader;
   }

   @Override
   protected void throwReaderNotFound()
   {
      throw new NotSupportedException(Messages.MESSAGES.couldNotFindMessageBodyReader(genericType, mediaType));
   }

   @Override
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
   public Object getProperty(String name)
   {
      return request.getAttribute(name);
   }

   @Override
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
   public void removeProperty(String name)
   {
      request.removeAttribute(name);
   }
}
