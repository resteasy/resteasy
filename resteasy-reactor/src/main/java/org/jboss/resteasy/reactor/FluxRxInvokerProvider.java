package org.jboss.resteasy.reactor;

import java.util.concurrent.ExecutorService;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FluxRxInvokerProvider implements RxInvokerProvider<FluxRxInvoker>
{
   WebTarget target;

   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return FluxRxInvoker.class.equals(clazz);
   }

   @Override
   public FluxRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      return new FluxRxInvokerImpl(syncInvoker, executorService);
   }

   public WebTarget getTarget()
   {
      return target;
   }

   public void setTarget(WebTarget target)
   {
      this.target = target;
   }
}
