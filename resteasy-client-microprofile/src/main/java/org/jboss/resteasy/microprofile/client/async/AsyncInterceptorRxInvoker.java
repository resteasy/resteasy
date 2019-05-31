package org.jboss.resteasy.microprofile.client.async;

import java.util.Collection;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptor;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerImpl;

/**
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 * @author <a href="mailto:asoldano@redhat.com">Alessio Soldano</a>
 * <p>
 */
public class AsyncInterceptorRxInvoker extends CompletionStageRxInvokerImpl
{
   public AsyncInterceptorRxInvoker(final SyncInvoker builder, final ExecutorService executor)
   {
      super(builder, executor);
   }

   public AsyncInterceptorRxInvoker(final SyncInvoker builder)
   {
      super(builder);
   }

   private static <T> CompletionStage<T> whenComplete(CompletionStage<T> stage) {
      final Collection<AsyncInvocationInterceptor> asyncInvocationInterceptors = AsyncInvocationInterceptorHandler.threadBoundInterceptors.get();
      AsyncInvocationInterceptorHandler.threadBoundInterceptors.remove();

      return stage.whenComplete((o, throwable) -> {
         if (asyncInvocationInterceptors != null ) {
            asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::removeContext);
         }
      });
   }

   @Override
   public CompletionStage<Response> get()
   {
      return whenComplete(super.get());
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return whenComplete(super.get(responseType));
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return whenComplete(super.get(responseType));
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return whenComplete(super.put(entity));
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return whenComplete(super.put(entity, clazz));
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return whenComplete(super.put(entity, type));
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return whenComplete(super.post(entity));
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      return whenComplete(super.post(entity, clazz));
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      return whenComplete(super.post(entity, type));
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return whenComplete(super.delete());
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return whenComplete(super.delete(responseType));
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return whenComplete(super.delete(responseType));
   }

   @Override
   public CompletionStage<Response> head()
   {
      return whenComplete(super.head());
   }

   @Override
   public CompletionStage<Response> options()
   {
      return whenComplete(super.options());
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return whenComplete(super.options(responseType));
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return whenComplete(super.options(responseType));
   }

   @Override
   public CompletionStage<Response> trace()
   {
      return whenComplete(super.trace());
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return whenComplete(super.trace(responseType));
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return whenComplete(super.trace(responseType));
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return whenComplete(super.method(name));
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return whenComplete(super.method(name, responseType));
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return whenComplete(super.method(name, responseType));
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return whenComplete(super.method(name, entity));
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return whenComplete(super.method(name, entity, responseType));
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return whenComplete(super.method(name, entity, responseType));
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return whenComplete(super.patch(entity));
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return whenComplete(super.patch(entity, responseType));
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return whenComplete(super.patch(entity, responseType));
   }
}
