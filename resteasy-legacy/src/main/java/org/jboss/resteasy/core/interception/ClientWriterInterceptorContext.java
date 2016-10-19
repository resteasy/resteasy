package org.jboss.resteasy.core.interception;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ClientWriterInterceptorContext instead.
 */
@Deprecated
public class ClientWriterInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.ClientWriterInterceptorContext
{

   public ClientWriterInterceptorContext(WriterInterceptor[] interceptors, ResteasyProviderFactory providerFactory,
         Object entity, Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> headers, OutputStream outputStream, Map<String, Object> properties)
   {
      super(interceptors, providerFactory, entity, type, genericType, annotations, mediaType, headers, outputStream,
            properties);
   }

}