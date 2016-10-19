package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ReaderInterceptor;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import static java.lang.String.format;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientReaderInterceptorContext extends AbstractReaderInterceptorContext
{
   protected Map<String, Object> properties;

   public ClientReaderInterceptorContext(ReaderInterceptor[] interceptors, ResteasyProviderFactory providerFactory, Class type,
                                         Type genericType, Annotation[] annotations, MediaType mediaType,
                                         MultivaluedMap<String, String> headers, InputStream inputStream,
                                         Map<String, Object> properties)
   {
      super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream);
      this.properties = properties;
   }

   protected void throwReaderNotFound()
   {
      throw new ProcessingException(Messages.MESSAGES.clientResponseFailureMediaType(mediaType, type));
   }

   @SuppressWarnings(value = "unchecked")
   @Override
   protected MessageBodyReader resolveReader(MediaType mediaType)
   {
      return providerFactory.getClientMessageBodyReader(type,
              genericType, annotations, mediaType);
   }

   @Override
   public Object getProperty(String name)
   {
      return properties.get(name);
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return properties.keySet();
   }

   @Override
   public void setProperty(String name, Object object)
   {
      if (object == null)
      {
         properties.remove(name);
      }
      else
      {
         properties.put(name, object);
      }
   }

   @Override
   public void removeProperty(String name)
   {
      properties.remove(name);
   }
}
