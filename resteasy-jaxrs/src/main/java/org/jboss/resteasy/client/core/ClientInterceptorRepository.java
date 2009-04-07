package org.jboss.resteasy.client.core;

import java.util.LinkedList;

import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyReaderInterceptor;
import org.jboss.resteasy.core.interception.MessageBodyWriterInterceptor;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */
public interface ClientInterceptorRepository {

	public LinkedList<MessageBodyReaderInterceptor> getReaderInterceptorList();

	public LinkedList<MessageBodyWriterInterceptor> getWriterInterceptorList();

	public LinkedList<ClientExecutionInterceptor> getExecutionInterceptorList();
}
