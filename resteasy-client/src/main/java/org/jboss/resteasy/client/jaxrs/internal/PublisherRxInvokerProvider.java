package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.ExecutorService;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;


public class PublisherRxInvokerProvider implements RxInvokerProvider<PublisherRxInvoker>
{
   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return PublisherRxInvoker.class.equals(clazz);
   }

   @Override
   public PublisherRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
      return new PublisherRxInvokerImpl(builder);
   }
}
