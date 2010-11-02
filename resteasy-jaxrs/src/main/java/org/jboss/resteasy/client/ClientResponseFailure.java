package org.jboss.resteasy.client;

import org.jboss.resteasy.client.core.BaseClientResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFailure extends RuntimeException
{
   private static final long serialVersionUID = 7491381058971118249L;
   private ClientResponse response;

   public ClientResponseFailure(ClientResponse response)
   {
      super("Failed with status: " + response.getStatus());
      this.response = BaseClientResponse.copyFromError(response);
      // release connection just in case it doesn't get garbage collected or manually released
      response.releaseConnection();
   }

   public ClientResponseFailure(String s, ClientResponse response)
   {
      super(s);
      this.response = BaseClientResponse.copyFromError(response);
      response.releaseConnection();
   }

   public ClientResponseFailure(String s, Throwable throwable, ClientResponse response)
   {
      super(s, throwable);
      this.response = BaseClientResponse.copyFromError(response);
      response.releaseConnection();
   }

   public ClientResponseFailure(Throwable throwable, ClientResponse response)
   {
      super(throwable);
      this.response = BaseClientResponse.copyFromError(response);
   }

   public ClientResponse getResponse()
   {
      return response;
   }
}
