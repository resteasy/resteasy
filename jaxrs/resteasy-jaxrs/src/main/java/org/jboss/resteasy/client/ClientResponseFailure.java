package org.jboss.resteasy.client;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFailure extends RuntimeException
{
   private ClientResponse response;

   public ClientResponseFailure(ClientResponse response)
   {
      super("Failed with status: " + response.getStatus());
      this.response = response;
   }

   public ClientResponseFailure(String s, ClientResponse response)
   {
      super(s);
      this.response = response;
   }

   public ClientResponseFailure(String s, Throwable throwable, ClientResponse response)
   {
      super(s, throwable);
      this.response = response;
   }

   public ClientResponseFailure(Throwable throwable, ClientResponse response)
   {
      super(throwable);
      this.response = response;
   }

   public ClientResponse getResponse()
   {
      return response;
   }
}
