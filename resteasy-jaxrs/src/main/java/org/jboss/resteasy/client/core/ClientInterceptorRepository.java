package org.jboss.resteasy.client.core;

import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.spi.interception.MessageBodyWriterInterceptor;

import java.util.LinkedList;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public interface ClientInterceptorRepository
{

   LinkedList<MessageBodyReaderInterceptor> getReaderInterceptorList();

   LinkedList<MessageBodyWriterInterceptor> getWriterInterceptorList();

   LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList();

   void registerInterceptor(Object interceptor);
}
