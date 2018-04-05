package org.jboss.resteasy.rxjava;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.ext.Provider;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
@SuppressWarnings("deprecation")
@Provider
public class ObservableRxInvokerProvider implements RxInvokerProvider<ObservableRxInvoker>
{
   WebTarget target;
   
   @Override
   public boolean isProviderFor(Class<?> clazz)
   {
      return ObservableRxInvoker.class.equals(clazz);
   }

   @Override
   public ObservableRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService)
   {
      return new ObservableRxInvokerImpl(syncInvoker, executorService);
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
