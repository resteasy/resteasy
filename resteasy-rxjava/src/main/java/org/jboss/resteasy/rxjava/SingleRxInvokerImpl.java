package org.jboss.resteasy.rxjava;

import javax.ws.rs.client.CompletionStageRxInvoker;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import rx.Single;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
@SuppressWarnings("unchecked")
public class SingleRxInvokerImpl implements SingleRxInvoker
{
   private final CompletionStageRxInvoker completionStageRxInvoker;
   private final SingleProvider singleProvider;

   public SingleRxInvokerImpl(CompletionStageRxInvoker completionStageRxInvoker)
   {
      this.completionStageRxInvoker = completionStageRxInvoker;
      this.singleProvider = new SingleProvider();
   }
   
   @Override
   public Single<Response> get()
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.get());
   }

   @Override
   public <T> Single<T> get(Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.get(responseType));
   }

   @Override
   public <T> Single<T> get(GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.get(responseType));
   }

   @Override
   public Single<Response> put(Entity<?> entity)
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity));
   }

   @Override
   public <T> Single<T> put(Entity<?> entity, Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public <T> Single<T> put(Entity<?> entity, GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.put(entity, responseType));
   }

   @Override
   public Single<Response> post(Entity<?> entity)
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.post(entity));
   }

   @Override
   public <T> Single<T> post(Entity<?> entity, Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public <T> Single<T> post(Entity<?> entity, GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.post(entity, responseType));
   }

   @Override
   public Single<Response> delete()
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.delete());
   }

   @Override
   public <T> Single<T> delete(Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.delete(responseType));
   }

   @Override
   public <T> Single<T> delete(GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.delete(responseType));
   }

   @Override
   public Single<Response> head()
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.head());
   }

   @Override
   public Single<Response> options()
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.options());
   }

   @Override
   public <T> Single<T> options(Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.options(responseType));
   }

   @Override
   public <T> Single<T> options(GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.options(responseType));
   }

   @Override
   public Single<Response> trace()
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.trace());
   }

   @Override
   public <T> Single<T> trace(Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.trace(responseType));
   }

   @Override
   public <T> Single<T> trace(GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.trace(responseType));
   }

   @Override
   public Single<Response> method(String name)
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name));
   }

   @Override
   public <T> Single<T> method(String name, Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public <T> Single<T> method(String name, GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, responseType));
   }

   @Override
   public Single<Response> method(String name, Entity<?> entity)
   {
      return (Single<Response>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity));
   }

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity, responseType));
   }

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return (Single<T>) singleProvider.fromCompletionStage(completionStageRxInvoker.method(name, entity, responseType));
   }
}
