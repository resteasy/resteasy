package org.jboss.resteasy.core.interception;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.AbstractReaderInterceptorContext instead.
 */
@Deprecated
public abstract class AbstractReaderInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.AbstractReaderInterceptorContext
{

   public AbstractReaderInterceptorContext(MediaType mediaType, ResteasyProviderFactory providerFactory,
         Annotation[] annotations, ReaderInterceptor[] interceptors, MultivaluedMap<String, String> headers,
         Type genericType, Class type, InputStream inputStream)
   {
      super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream);
   }

}
