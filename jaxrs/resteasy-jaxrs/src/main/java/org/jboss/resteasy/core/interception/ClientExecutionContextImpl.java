package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientExecutionContextImpl implements ClientExecutionContext
{
   protected ClientExecutionInterceptor[] interceptors;
   protected ClientExecutor executor;
   protected ClientRequest request;
   protected int index = 0;

   public ClientExecutionContextImpl(ClientExecutionInterceptor[] interceptors, ClientExecutor executor, ClientRequest request)
   {
      this.interceptors = interceptors;
      this.executor = executor;
      this.request = request;
   }

   public ClientRequest getRequest()
   {
      return request;
   }

   public ClientResponse proceed() throws Exception
   {
      if (index >= interceptors.length)
      {
         return executor.execute(request);
      }
      else
      {
         try
         {
            return interceptors[index++].execute(this);
         }
         finally
         {
            index--;
         }
      }
   }
}