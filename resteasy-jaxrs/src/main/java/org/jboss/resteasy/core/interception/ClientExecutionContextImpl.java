package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.spi.interception.ClientExecutionContext;
import org.jboss.resteasy.spi.interception.ClientExecutionInterceptor;

import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see <a href="https://jcp.org/en/jsr/detail?id=339">jaxrs-api</a>
 * 
 */
@Deprecated
public class ClientExecutionContextImpl implements ClientExecutionContext
{
   protected List<ClientExecutionInterceptor> interceptors;
   protected ClientExecutor executor;
   protected ClientRequest request;
   protected int index = 0;

   public ClientExecutionContextImpl(List<ClientExecutionInterceptor> interceptors, ClientExecutor executor, ClientRequest request)
   {
      this.interceptors = interceptors;
      this.executor = executor;
      this.request = request;
   }

   public ClientRequest getRequest()
   {
      return request;
   }

   @SuppressWarnings("unchecked")
   public ClientResponse proceed() throws Exception
   {
      if (index >= interceptors.size())
      {
         return executor.execute(request);
      }
      else
      {
         try
         {
            return interceptors.get(index++).execute(this);
         }
         finally
         {
            index--;
         }
      }
   }
}