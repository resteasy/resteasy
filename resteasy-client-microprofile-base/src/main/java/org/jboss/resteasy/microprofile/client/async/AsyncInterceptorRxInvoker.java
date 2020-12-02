package org.jboss.resteasy.microprofile.client.async;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.CompletionStageRxInvokerImpl;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.microprofile.client.ExceptionMapping;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

/**
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 * @author <a href="mailto:asoldano@redhat.com">Alessio Soldano</a>
 * <p>
 */
public class AsyncInterceptorRxInvoker extends CompletionStageRxInvokerImpl
{
   private Method method;

   public AsyncInterceptorRxInvoker(final SyncInvoker builder, final ExecutorService executor)
   {
      super(builder, executor);
      setupMethod(builder);
   }

   public AsyncInterceptorRxInvoker(final SyncInvoker builder)
   {
      super(builder);
      setupMethod(builder);
   }

   private void setupMethod(SyncInvoker builder)
   {
      // we must capture the method to unwrap the exception
      method = ((ClientInvocationBuilder)builder).getClientInvocation().getClientInvoker().getMethod();
   }

   private static <T> CompletionStage<T> whenComplete(CompletionStage<T> stage, Method method) {

      return stage.handle((ret, t) -> {
         if(t != null) {
            if(t instanceof CompletionException) {
               t = t.getCause();
            }
            if (t instanceof ExceptionMapping.HandlerException) {
               try
               {
                  // make sure we map the exception in async mode
                  ((ExceptionMapping.HandlerException)t).mapException(method);
               } catch (Exception e)
               {
                  SynchronousDispatcher.rethrow(e);
               }
           }
           // don't forget to rethrow
           SynchronousDispatcher.rethrow(t);
         }
         return ret;
      });
   }

   @Override
   public CompletionStage<Response> get()
   {
      return whenComplete(super.get(), method);
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return whenComplete(super.get(responseType), method);
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return whenComplete(super.get(responseType), method);
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return whenComplete(super.put(entity), method);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return whenComplete(super.put(entity, clazz), method);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return whenComplete(super.put(entity, type), method);
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return whenComplete(super.post(entity), method);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      return whenComplete(super.post(entity, clazz), method);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
      return whenComplete(super.post(entity, type), method);
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return whenComplete(super.delete(), method);
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return whenComplete(super.delete(responseType), method);
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return whenComplete(super.delete(responseType), method);
   }

   @Override
   public CompletionStage<Response> head()
   {
      return whenComplete(super.head(), method);
   }

   @Override
   public CompletionStage<Response> options()
   {
      return whenComplete(super.options(), method);
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return whenComplete(super.options(responseType), method);
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return whenComplete(super.options(responseType), method);
   }

   @Override
   public CompletionStage<Response> trace()
   {
      return whenComplete(super.trace(), method);
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return whenComplete(super.trace(responseType), method);
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return whenComplete(super.trace(responseType), method);
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return whenComplete(super.method(name), method);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return whenComplete(super.method(name, responseType), method);
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return whenComplete(super.method(name, responseType), method);
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return whenComplete(super.method(name, entity), method);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return whenComplete(super.method(name, entity, responseType), method);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return whenComplete(super.method(name, entity, responseType), method);
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return whenComplete(super.patch(entity), method);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return whenComplete(super.patch(entity, responseType), method);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return whenComplete(super.patch(entity, responseType), method);
   }
}
