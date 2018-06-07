package org.jboss.resteasy.rxjava;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.rxjava.i18n.Messages;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
@Deprecated
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
         throw new ProcessingException(Messages.MESSAGES.expectedClientInvocationBuilder(syncInvoker.getClass().getName()));
      }
   }
}
