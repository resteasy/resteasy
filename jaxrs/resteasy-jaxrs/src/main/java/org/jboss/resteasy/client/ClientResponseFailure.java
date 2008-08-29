package org.jboss.resteasy.client;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientResponseFailure extends RuntimeException
{
   private ClientResponse<byte[]> response;

   public ClientResponseFailure(ClientResponse<byte[]> response)
   {
      this.response = response;
   }

   public ClientResponseFailure(String s, ClientResponse<byte[]> response)
   {
      super(s);
      this.response = response;
   }

   public ClientResponseFailure(String s, Throwable throwable, ClientResponse<byte[]> response)
   {
      super(s, throwable);
      this.response = response;
   }

   public ClientResponseFailure(Throwable throwable, ClientResponse<byte[]> response)
   {
      super(throwable);
      this.response = response;
   }

   public ClientResponse<byte[]> getResponse()
   {
      return response;
   }
}
