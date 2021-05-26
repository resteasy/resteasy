package org.jboss.resteasy.rso;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.Provider;

@Provider
public class PublisherRxInvokerProvider implements RxInvokerProvider<PublisherRxInvoker>
{
   WebTarget target;

   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return PublisherRxInvoker.class.equals(clazz);
   }

   @Override
   public PublisherRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      return new PublisherRxInvokerImpl(syncInvoker, executorService);
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
