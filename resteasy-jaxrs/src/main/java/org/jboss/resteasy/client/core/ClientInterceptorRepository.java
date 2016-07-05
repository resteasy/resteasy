package org.jboss.resteasy.client.core;

import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;
import java.util.LinkedList;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public interface ClientInterceptorRepository
{

   LinkedList<ReaderInterceptor> getReaderInterceptorList();

   LinkedList<WriterInterceptor> getWriterInterceptorList();

   LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList();

   void registerInterceptor(Object interceptor);
}
