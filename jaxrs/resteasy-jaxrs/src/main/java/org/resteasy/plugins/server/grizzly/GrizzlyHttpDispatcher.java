package org.resteasy.plugins.server.grizzly;

import com.sun.grizzly.tcp.http11.GrizzlyRequest;
import com.sun.grizzly.tcp.http11.GrizzlyResponse;
import org.resteasy.spi.HttpResponse;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;

import java.io.IOException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class GrizzlyHttpDispatcher extends AbstractGrizzlyDispatcher
{
   public GrizzlyHttpDispatcher(ResteasyProviderFactory providerFactory, Registry registry, String contextPath)
   {
      super(providerFactory, registry, contextPath);
   }

   private static class GrizzlyResponseWrapper extends GrizzlyResponse
   {
      private GrizzlyResponse wrapper;

   }

   public void service(GrizzlyRequest request, GrizzlyResponse response) throws IOException
   {
      invokeJaxrs(request, response);
   }

   @Override
   protected HttpResponse createHttpResponse(GrizzlyResponse response)
   {
      HttpResponse theResponse = new GrizzlyHttpResponse(response, dispatcher.getProviderFactory())
      {
         @Override
         public void sendError(int status, String message) throws IOException
         {
            response.setAppCommitted(true);

            if (response.isCommitted())
               throw new IllegalStateException("response is already committed");

            response.setError();

            response.getResponse().setStatus(status);
            response.getResponse().setMessage(message);

            // Clear any data content that has been buffered
            response.resetBuffer();

         }
      };
      return theResponse;
   }
}
