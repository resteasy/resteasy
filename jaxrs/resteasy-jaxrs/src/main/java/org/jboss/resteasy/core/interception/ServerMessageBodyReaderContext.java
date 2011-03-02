package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerMessageBodyReaderContext extends MessageBodyReaderContextImpl
{
   private HttpRequest request;

   public ServerMessageBodyReaderContext(MessageBodyReaderInterceptor[] interceptors, MessageBodyReader reader, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream inputStream, HttpRequest request)
   {
      super(interceptors, reader, type, genericType, annotations, mediaType, headers, inputStream);
      this.request = request;
   }

   @Override
   public Object getAttribute(String attribute)
   {
      return request.getAttribute(attribute);
   }

   @Override
   public void setAttribute(String name, Object value)
   {
      request.setAttribute(name, value);
   }

   @Override
   public void removeAttribute(String name)
   {
      request.removeAttribute(name);
   }
}
