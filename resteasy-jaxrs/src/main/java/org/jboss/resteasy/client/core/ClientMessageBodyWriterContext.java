package org.jboss.resteasy.client.core;

import org.jboss.resteasy.core.interception.MessageBodyWriterContextImpl;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientMessageBodyWriterContext extends MessageBodyWriterContextImpl
{
   Map<String, Object> attributes;

   public ClientMessageBodyWriterContext(MessageBodyWriterInterceptor[] interceptors, MessageBodyWriter writer, Object entity, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream outputStream, Map<String, Object> attributes)
   {
      super(interceptors, writer, entity, type, genericType, annotations, mediaType, headers, outputStream);
      this.attributes = attributes;
   }

   @Override
   public Object getAttribute(String attribute)
   {
      return attributes.get(attribute);
   }

   @Override
   public void setAttribute(String name, Object value)
   {
      attributes.put(name, value);
   }

   @Override
   public void removeAttribute(String name)
   {
      attributes.remove(name);
   }
}
