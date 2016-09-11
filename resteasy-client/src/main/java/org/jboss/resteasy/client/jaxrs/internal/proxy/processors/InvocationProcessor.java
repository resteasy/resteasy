package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InvocationProcessor
{
   void process(ClientInvocation invocation, Object param);
}
