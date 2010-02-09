package org.jboss.resteasy.client;

import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ClientExecutor
{
   ClientRequest createRequest(String uriTemplate);

   ClientRequest createRequest(UriBuilder uriBuilder);

   ClientResponse execute(ClientRequest request) throws Exception;
}
