package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.ReaderInterceptor;
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
@SuppressWarnings("rawtypes")
public class ServerReaderInterceptorContext extends AbstractReaderInterceptorContext
{
   private HttpRequest request;

   public ServerReaderInterceptorContext(final ReaderInterceptor[] interceptors, final ResteasyProviderFactory providerFactory, final Class type,
                                         final Type genericType, final Annotation[] annotations, final MediaType mediaType,
                                         final MultivaluedMap<String, String> headers, final InputStream inputStream,
                                         final HttpRequest request)
   {
      super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream, RESTEasyTracingLogger.getInstance(request));
      this.request = request;
   }

   @Override
   protected MessageBodyReader resolveReader(MediaType mediaType)
   {
      @SuppressWarnings(value = "unchecked")
      MessageBodyReader reader =  ((ResteasyProviderFactoryImpl)providerFactory).getServerMessageBodyReader(type,
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
