package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class handles client errors (of course...).
 *
 * @author Solomon.Duskis
 * 
 * @deprecated The Resteasy client framework in resteasy-jaxrs is replaced by
 *             the JAX-RS 2.0 compliant resteasy-client module.
 *             
 *             The Resteasy client proxy framework is replaced by the client proxy
 *             framework in resteasy-client module.
 *             
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */

// TODO: expand this class for more robust, complicated error handling
@Deprecated
public class ClientErrorHandler
{
   private List<ClientErrorInterceptor> interceptors;

   public ClientErrorHandler(List<ClientErrorInterceptor> interceptors)
   {
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
