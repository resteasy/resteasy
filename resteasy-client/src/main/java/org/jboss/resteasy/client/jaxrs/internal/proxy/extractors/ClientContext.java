package org.jboss.resteasy.client.jaxrs.internal.proxy.extractors;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

public class ClientContext
{
   private ClientInvocation invocation;
   private ClientResponse clientResponse;
   private EntityExtractorFactory extractorFactory;

   public ClientContext(final ClientInvocation invocation, final ClientResponse clientResponse, final EntityExtractorFactory extractorFactory)
   {
      this.invocation = invocation;
      this.clientResponse = clientResponse;
      this.extractorFactory = extractorFactory;
   }

   public ClientInvocation getInvocation()
   {
      return invocation;
   }

   public ClientResponse getClientResponse()
   {
      return clientResponse;
   }

   public EntityExtractorFactory getExtractorFactory()
   {
      return extractorFactory;
   }
}
