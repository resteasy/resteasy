package org.jboss.resteasy.core.interception;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.ServerReaderInterceptorContext instead.
 */
@Deprecated
public class ServerReaderInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.ServerReaderInterceptorContext
{

   public ServerReaderInterceptorContext(ReaderInterceptor[] interceptors, ResteasyProviderFactory providerFactory,
         Class type, Type genericType, Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> headers, InputStream inputStream, HttpRequest request)
   {
      super(interceptors, providerFactory, type, genericType, annotations, mediaType, headers, inputStream, request);
   }

}
