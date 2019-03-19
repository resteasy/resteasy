package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/**
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 * <p>
 * Date March 9, 2016
 */
public class CompletionStageRxInvokerImpl implements CompletionStageRxInvoker
{
   private final Invocation.Builder builder;

   private final ExecutorService executor;

   public CompletionStageRxInvokerImpl(final SyncInvoker builder)
   {
      this(builder, null);
   }

   public CompletionStageRxInvokerImpl(final SyncInvoker builder, final ExecutorService executor)
   {
      this.builder = (Invocation.Builder)builder;
      this.executor = executor;
   }

   @Override
   public CompletionStage<Response> get()
   {
      return toCompletableFuture(callback -> builder.async().get(callback));
   }

   @Override
   public <T> CompletionStage<T> get(Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().get(callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> get(GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().get(callback), responseType);
   }

   @Override
   public CompletionStage<Response> put(Entity<?> entity)
   {
      return toCompletableFuture(callback -> builder.async().put(entity, callback));
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, Class<T> clazz)
   {
      return toCompletableFuture(callback -> builder.async().put(entity, callback), clazz);
   }

   @Override
   public <T> CompletionStage<T> put(Entity<?> entity, GenericType<T> type)
   {
      return toCompletableFuture(callback -> builder.async().put(entity, callback), type);
   }

   @Override
   public CompletionStage<Response> post(Entity<?> entity)
   {
      return toCompletableFuture(callback -> builder.async().post(entity, callback));
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, Class<T> clazz)
   {
      return toCompletableFuture(callback -> builder.async().post(entity, callback), clazz);
   }

   @Override
   public <T> CompletionStage<T> post(Entity<?> entity, GenericType<T> type)
   {
       return toCompletableFuture(callback -> builder.async().post(entity, callback), type);
   }

   @Override
   public CompletionStage<Response> delete()
   {
      return toCompletableFuture(callback -> builder.async().delete(callback));
   }

   @Override
   public <T> CompletionStage<T> delete(Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().delete(callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> delete(GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().delete(callback), responseType);
   }

   @Override
   public CompletionStage<Response> head()
   {
      return toCompletableFuture(callback -> builder.async().head(callback));
   }

   @Override
   public CompletionStage<Response> options()
   {
      return toCompletableFuture(callback -> builder.async().options(callback));
   }

   @Override
   public <T> CompletionStage<T> options(Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().options(callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> options(GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().options(callback), responseType);
   }

   @Override
   public CompletionStage<Response> trace()
   {
      return toCompletableFuture(callback -> builder.async().trace(callback));
   }

   @Override
   public <T> CompletionStage<T> trace(Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().trace(callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> trace(GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().trace(callback), responseType);
   }

   @Override
   public CompletionStage<Response> method(String name)
   {
      return toCompletableFuture(callback -> builder.async().method(name, callback));
   }

   @Override
   public <T> CompletionStage<T> method(String name, Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(name, callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(name, callback), responseType);
   }

   @Override
   public CompletionStage<Response> method(String name, Entity<?> entity)
   {
      return toCompletableFuture(callback -> builder.async().method(name, entity, callback));
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(name, entity, callback), responseType);
   }

   @Override
   public <T> CompletionStage<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(name, entity, callback), responseType);
   }

   public ExecutorService getExecutor()
   {
      return executor;
   }

   public CompletionStage<Response> patch(Entity<?> entity)
   {
      return toCompletableFuture(callback -> builder.async().method(HttpMethod.PATCH, entity, callback));
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, Class<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(HttpMethod.PATCH, entity, callback), responseType);
   }

   public <T> CompletionStage<T> patch(Entity<?> entity, GenericType<T> responseType)
   {
      return toCompletableFuture(callback -> builder.async().method(HttpMethod.PATCH, entity, callback), responseType);
   }

   private <T> CompletionStage<T> toCompletableFuture(Function<InvocationCallback<Response>, Future<Response>> f,
                                                      final Class<T> responseType) {

      CompletableFuture<Response> completableFuture = toCompletableFuture(f);

      if (Response.class.equals(responseType))
         return (CompletableFuture<T>)completableFuture;

      return completableFuture.thenApply(
              response -> ClientInvocation.extractResult(new GenericType<T>(responseType), response, null));
   }

   private <T> CompletionStage<T> toCompletableFuture(Function<InvocationCallback<Response>, Future<Response>> f,
                                                      final GenericType<T> responseType) {

      CompletableFuture<Response> completableFuture = toCompletableFuture(f);

       if (responseType.getRawType().equals(Response.class))
          return (CompletableFuture<T>)completableFuture;

      return completableFuture.thenApply(
              response -> ClientInvocation.extractResult(responseType, response, null));
   }

   private CompletableFuture<Response> toCompletableFuture(Function<InvocationCallback<Response>, Future<Response>> f) {

      final CompletableFuture<Response> completableFuture = new CompletableFuture<>();

      f.apply(new InvocationCallback<Response>() {
         @Override
         public void completed(Response response) {
            response.bufferEntity();
            completableFuture.complete(response);
         }

         @Override
         public void failed(Throwable throwable) {
            completableFuture.completeExceptionally(throwable);
         }
      });

      return completableFuture;
   }
}
