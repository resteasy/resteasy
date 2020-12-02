package org.jboss.resteasy.reactor;

import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import reactor.core.publisher.Mono;

@SuppressWarnings("unchecked")
public class MonoRxInvokerImpl implements MonoRxInvoker
{
   private final CompletionStageRxInvoker completionStageRxInvoker;
   private final MonoProvider monoProvider;

   public MonoRxInvokerImpl(final CompletionStageRxInvoker completionStageRxInvoker)
   {
      this.completionStageRxInvoker = completionStageRxInvoker;
      this.monoProvider = new MonoProvider();
   }

   @Override
   public Mono<Response> get()
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.get());
   }

   @Override
   public <T> Mono<T> get(Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.get(responseType));
   }

   @Override
   public <T> Mono<T> get(GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.get(responseType));
   }

   @Override
   public Mono<Response> put(Entity<?> entity)
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.put(entity));
   }

   @Override
   public <T> Mono<T> put(Entity<?> entity, Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public <T> Mono<T> put(Entity<?> entity, GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public Mono<Response> post(Entity<?> entity)
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.post(entity));
   }

   @Override
   public <T> Mono<T> post(Entity<?> entity, Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public <T> Mono<T> post(Entity<?> entity, GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public Mono<Response> delete()
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.delete());
   }

   @Override
   public <T> Mono<T> delete(Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.delete(responseType));
   }

   @Override
   public <T> Mono<T> delete(GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.delete(responseType));
   }

   @Override
   public Mono<Response> head()
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.head());
   }

   @Override
   public Mono<Response> options()
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.options());
   }

   @Override
   public <T> Mono<T> options(Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.options(responseType));
   }

   @Override
   public <T> Mono<T> options(GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.options(responseType));
   }

   @Override
   public Mono<Response> trace()
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.trace());
   }

   @Override
   public <T> Mono<T> trace(Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.trace(responseType));
   }

   @Override
   public <T> Mono<T> trace(GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.trace(responseType));
   }

   @Override
   public Mono<Response> method(String name)
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name));
   }

   @Override
   public <T> Mono<T> method(String name, Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public <T> Mono<T> method(String name, GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public Mono<Response> method(String name, Entity<?> entity)
   {
      return (Mono<Response>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name, entity));
   }

   @Override
   public <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name, entity, responseType));
   }

   @Override
   public <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return (Mono<T>) monoProvider.fromCompletionStage(() -> completionStageRxInvoker.method(name, entity, responseType));
   }
}
