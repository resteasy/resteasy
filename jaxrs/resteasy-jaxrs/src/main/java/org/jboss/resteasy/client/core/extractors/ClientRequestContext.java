package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

import java.net.URI;

/**
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by
 *             the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 * 
 * @see org.jboss.resteasy.client.jaxrs.internal.proxy.extractors.ClientContext
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
public class ClientRequestContext
{
   private ClientRequest request;
   private BaseClientResponse<?> clientResponse;
   private ClientErrorHandler errorHandler;
   private EntityExtractorFactory extractorFactory;
   private URI baseURI;

   public ClientRequestContext(ClientRequest request, BaseClientResponse<?> clientResponse,
                               ClientErrorHandler errorHandler, EntityExtractorFactory extractorFactory, URI baseURI)
   {
      this.request = request;
      this.clientResponse = clientResponse;
      this.errorHandler = errorHandler;
      this.extractorFactory = extractorFactory;
      this.baseURI = baseURI;
   }

   public ClientRequest getRequest()
   {
      return request;
   }

   public BaseClientResponse getClientResponse()
   {
      return clientResponse;
   }

   public ClientErrorHandler getErrorHandler()
   {
      return errorHandler;
   }

   public EntityExtractorFactory getExtractorFactory()
   {
      return extractorFactory;
   }

   public URI getBaseURI()
   {
      return baseURI;
   }
}
