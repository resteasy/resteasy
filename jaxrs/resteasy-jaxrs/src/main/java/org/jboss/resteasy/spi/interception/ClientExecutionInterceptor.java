package org.jboss.resteasy.spi.interception;

import org.jboss.resteasy.client.ClientResponse;

/**
 * @deprecated
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Deprecated
public interface ClientExecutionInterceptor
{
   ClientResponse execute(ClientExecutionContext ctx) throws Exception;
}
