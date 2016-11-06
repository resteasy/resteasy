package org.jboss.resteasy.core.interception;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use org.jboss.resteasy.core.interception.jaxrs.AbstractWriterInterceptorContext instead.
 */
@Deprecated
public abstract class AbstractWriterInterceptorContext extends org.jboss.resteasy.core.interception.jaxrs.AbstractWriterInterceptorContext
{

   public AbstractWriterInterceptorContext(WriterInterceptor[] interceptors, Annotation[] annotations, Object entity,
         Type genericType, MediaType mediaType, Class type, OutputStream outputStream,
         ResteasyProviderFactory providerFactory, MultivaluedMap<String, Object> headers)
   {
      super(interceptors, annotations, entity, genericType, mediaType, type, outputStream, providerFactory, headers);
   }

}
