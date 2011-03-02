package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ServerMessageBodyWriterContext extends MessageBodyWriterContextImpl
{
   private HttpRequest request;

   public ServerMessageBodyWriterContext(MessageBodyWriterInterceptor[] interceptors, MessageBodyWriter writer, Object entity, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> headers, OutputStream outputStream, HttpRequest request)
   {
      super(interceptors, writer, entity, type, genericType, annotations, mediaType, headers, outputStream);
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
