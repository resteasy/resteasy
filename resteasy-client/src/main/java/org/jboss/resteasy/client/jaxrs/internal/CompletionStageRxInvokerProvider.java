package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

public class CompletionStageRxInvokerProvider implements RxInvokerProvider<CompletionStageRxInvoker>
{
   @Override
   public boolean isProviderFor(Class<?> clazz) {
      return CompletionStageRxInvoker.class.equals(clazz);
   }

   @Override
   public CompletionStageRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
      return new CompletionStageRxInvokerImpl(syncInvoker, executorService);
   }
}
