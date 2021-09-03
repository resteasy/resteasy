package org.jboss.resteasy.rxjava2;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.ext.Provider;

@Provider
public class FlowableRxInvokerProvider implements RxInvokerProvider<FlowableRxInvoker>
{
   WebTarget target;

   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return FlowableRxInvoker.class.equals(clazz);
   }

   @Override
   public FlowableRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      return new FlowableRxInvokerImpl(syncInvoker, executorService);
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
