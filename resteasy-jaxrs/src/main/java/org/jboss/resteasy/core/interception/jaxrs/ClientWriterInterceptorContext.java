package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.resteasy_jaxrs.i18n.*;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.WriterInterceptor;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientWriterInterceptorContext extends AbstractWriterInterceptorContext
{
   protected Map<String, Object> properties;

   public ClientWriterInterceptorContext(WriterInterceptor[] interceptors, ResteasyProviderFactory providerFactory,
                                         Object entity, Class type, Type genericType, Annotation[] annotations,
                                         MediaType mediaType, MultivaluedMap<String, Object> headers,
                                         OutputStream outputStream, Map<String, Object> properties)
   {
      super(interceptors, annotations, entity, genericType, mediaType, type, outputStream, providerFactory, headers);
      this.properties = properties;
   }

   @Override
   void throwWriterNotFoundException()
   {
      throw new ProcessingException(Messages.MESSAGES.couldNotFindWriterForContentType(mediaType, type.getName()));
   }

   @Override
   protected MessageBodyWriter resolveWriter()
   {
      @SuppressWarnings(value = "unchecked")
      MessageBodyWriter writer = providerFactory.getClientMessageBodyWriter(
              type, genericType, annotations, mediaType);
      //logger.info("********* WRITER: " + writer.getClass().getName());
      return writer;
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
