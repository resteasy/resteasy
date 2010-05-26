/**
 * 
 */
package org.jboss.resteasy.client.core.extractors;

import java.io.InputStream;
import java.lang.reflect.Method;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

@SuppressWarnings("unchecked")
public class BodyEntityExtractor implements EntityExtractor
{
   private final Class returnType;
   private final Method method;
   private ClientErrorHandler handler;

   public BodyEntityExtractor(Class returnType, Method method, ClientErrorHandler handler)
   {
      this.handler = handler;
      this.returnType = returnType;
      this.method = method;
   }

   public Object extractEntity(ClientRequest request, BaseClientResponse clientResponse)
   {
      try
      {
         clientResponse.checkFailureStatus();
      }
      catch (RuntimeException e)
      {
         handler.clientErrorHandling(clientResponse, e);
      }

      // only release connection if it is not an instance of an
      // InputStream
      boolean releaseConnectionAfter = true;
      try
      {

         if (returnType == null || ClientExtractorUtility.isVoidReturnType(returnType))
         {
            // the connection will be released in the finally block
            return null;
         }

         clientResponse.setReturnType(returnType);
         clientResponse.setGenericReturnType(method.getGenericReturnType());

         Object obj = clientResponse.getEntity();
         if (obj instanceof InputStream)
            releaseConnectionAfter = false;
         return obj;
      }
      catch (RuntimeException e)
      {
         handler.clientErrorHandling(clientResponse, e);
      }
      finally
      {
         if (releaseConnectionAfter)
            clientResponse.releaseConnection();
      }
      throw new RuntimeException("Should be unreachable");
   }
}