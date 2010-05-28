package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

public class ClientRequestContext
{
   private ClientRequest request;
   private BaseClientResponse<?> clientResponse;
   private ClientErrorHandler errorHandler;

   public ClientRequestContext(ClientRequest request, BaseClientResponse<?> clientResponse, ClientErrorHandler errorHandler)
   {
      super();
      this.request = request;
      this.clientResponse = clientResponse;
      this.errorHandler = errorHandler;
   }

   public ClientRequest getRequest()
   {
      return request;
   }

   @SuppressWarnings("unchecked")
   public BaseClientResponse getClientResponse()
   {
      return clientResponse;
   }

   public ClientErrorHandler getErrorHandler()
   {
      return errorHandler;
   }

}
