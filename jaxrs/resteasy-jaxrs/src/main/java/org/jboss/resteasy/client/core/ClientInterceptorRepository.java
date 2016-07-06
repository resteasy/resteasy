package org.jboss.resteasy.client.core;

import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import java.util.LinkedList;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs
 *             is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy interceptor facility introduced in release 2.x
 *             is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
public interface ClientInterceptorRepository
{

   LinkedList<ReaderInterceptor> getReaderInterceptorList();

   LinkedList<WriterInterceptor> getWriterInterceptorList();

   LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList();

   void registerInterceptor(Object interceptor);
}
