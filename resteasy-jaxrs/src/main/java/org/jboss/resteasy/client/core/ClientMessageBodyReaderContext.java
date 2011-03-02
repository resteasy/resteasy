package org.jboss.resteasy.client.core;

import org.jboss.resteasy.core.interception.MessageBodyReaderContextImpl;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientMessageBodyReaderContext extends MessageBodyReaderContextImpl
{
   Map<String, Object> attributes;

   public ClientMessageBodyReaderContext(MessageBodyReaderInterceptor[] interceptors, MessageBodyReader reader, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream inputStream, Map<String, Object> attributes)
   {
      super(interceptors, reader, type, genericType, annotations, mediaType, headers, inputStream);
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
