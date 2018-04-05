package org.jboss.resteasy.rxjava2;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;

public class SingleRxInvokerProvider implements RxInvokerProvider<SingleRxInvoker>
{
   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return SingleRxInvoker.class.equals(clazz);
   }

   @Override
   public SingleRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      if (syncInvoker instanceof ClientInvocationBuilder)
      {
         ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
         CompletionStageRxInvoker completionStageRxInvoker = builder.rx();
         return new SingleRxInvokerImpl(completionStageRxInvoker);
      }
      else
      {
         throw new RuntimeException("Expected ClientInvocationBuilder"); // @TODO i18n
      }
   }
}
