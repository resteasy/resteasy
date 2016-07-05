package org.jboss.resteasy.client;

import javax.ws.rs.core.UriBuilder;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by the JAX-RS 2.0 compliant resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.ClientHttpEngine
 */
@Deprecated
public interface ClientExecutor
{
   ClientRequest createRequest(String uriTemplate);

   ClientRequest createRequest(UriBuilder uriBuilder);

   ClientResponse execute(ClientRequest request) throws Exception;
   
   void close() throws Exception;
}
