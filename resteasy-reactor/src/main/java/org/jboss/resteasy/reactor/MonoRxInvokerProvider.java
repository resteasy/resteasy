package org.jboss.resteasy.reactor;

import java.util.concurrent.ExecutorService;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.reactor.i18n.Messages;

public class MonoRxInvokerProvider implements RxInvokerProvider<MonoRxInvoker>
{
   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return MonoRxInvoker.class.equals(clazz);
   }

   @Override
   public MonoRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      if (syncInvoker instanceof ClientInvocationBuilder)
      {
         ClientInvocationBuilder builder = (ClientInvocationBuilder) syncInvoker;
         CompletionStageRxInvoker completionStageRxInvoker = builder.rx();
         return new MonoRxInvokerImpl(completionStageRxInvoker);
      }
      else
      {
         throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
      }
   }
}
