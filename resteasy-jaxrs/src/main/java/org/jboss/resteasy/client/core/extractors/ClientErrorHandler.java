package org.jboss.resteasy.client.core.extractors;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;

public class ClientErrorHandler
{
   private List<ClientErrorInterceptor> interceptors;

   public ClientErrorHandler(List<ClientErrorInterceptor> interceptors)
   {
      super();
      this.interceptors = interceptors;
   }

   @SuppressWarnings("unchecked")
   public void clientErrorHandling(BaseClientResponse clientResponse, RuntimeException e)
   {
      for (ClientErrorInterceptor handler : interceptors)
      {
         try
         {
            // attempt to reset the stream in order to provide a fresh stream
            // to each ClientErrorInterceptor -- failing to reset the stream
            // could mean that an unusable stream will be passed to the
            // interceptor
            InputStream stream = clientResponse.getStreamFactory().getInputStream();
            if (stream != null)
            {
               stream.reset();
            }
         }
         catch (IOException e1)
         {
            // eat this exception since it's not really relevant for the client
            // response
         }
         handler.handle(clientResponse);
      }
      throw e;
   }
}
