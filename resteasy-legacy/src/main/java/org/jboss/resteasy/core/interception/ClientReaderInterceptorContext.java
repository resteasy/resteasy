package org.jboss.resteasy.core.interception;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ClientReaderInterceptorContext instead.
 */
@Deprecated
public class ClientReaderInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.ClientReaderInterceptorContext
{

   public ClientReaderInterceptorContext(ReaderInterceptor[] interceptors, ResteasyProviderFactory providerFactory,
         Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> headers, InputStream inputStream, Map<String, Object> properties)
   {
      super(interceptors, providerFactory, type, genericType, annotations, mediaType, headers, inputStream, properties);
   }

}
