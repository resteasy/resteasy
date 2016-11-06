package org.jboss.resteasy.core.interception;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ServerWriterInterceptorContext instead.
 */
@Deprecated
public class ServerWriterInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.ServerWriterInterceptorContext
{

   public ServerWriterInterceptorContext(WriterInterceptor[] interceptors, ResteasyProviderFactory providerFactory,
         Object entity, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> headers, OutputStream outputStream, HttpRequest request)
   {
      super(interceptors, providerFactory, entity, type, genericType, annotations, mediaType, headers, outputStream, request);
   }

}
