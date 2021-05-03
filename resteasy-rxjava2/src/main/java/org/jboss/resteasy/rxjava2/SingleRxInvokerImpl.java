package org.jboss.resteasy.rxjava2;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.PublisherRxInvokerImpl;
import org.reactivestreams.Publisher;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

@SuppressWarnings("unchecked")
public class SingleRxInvokerImpl implements SingleRxInvoker
{

   private final SinglePublisherInvoker publisherInvoker;

   public SingleRxInvokerImpl(ClientInvocationBuilder builder)
   {
      publisherInvoker = new SinglePublisherInvoker(Objects.requireNonNull(builder));
   }


   private static <T> Single<T> toSingle(Publisher<T> publisher) {
      return Single.fromPublisher(publisher);
   }

   static class SinglePublisherInvoker extends PublisherRxInvokerImpl
   {
      public SinglePublisherInvoker(final ClientInvocationBuilder builder)
      {
         super(builder);
      }

      @Override
      protected <T> Publisher<T> toPublisher(CompletionStage<T> completable) {
         return Flowable.fromFuture(completable.toCompletableFuture());
      }
   }

   @Override
   public Single<Response> get()
   {
      return toSingle(publisherInvoker.get());
   }

   @Override
   public <T> Single<T> get(Class<T> responseType)
   {
      return toSingle(publisherInvoker.get(responseType));
   }

   @Override
   public <T> Single<T> get(GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.get(responseType));
   }

   @Override
   public Single<Response> put(Entity<?> entity)
   {
      return toSingle(publisherInvoker.put(entity));
   }

   @Override
   public <T> Single<T> put(Entity<?> entity, Class<T> clazz)
   {
      return toSingle(publisherInvoker.put(entity, clazz));
   }

   @Override
   public <T> Single<T> put(Entity<?> entity, GenericType<T> type)
   {
      return toSingle(publisherInvoker.put(entity, type));
   }

   @Override
   public Single<Response> post(Entity<?> entity)
   {
      return toSingle(publisherInvoker.post(entity));
   }

   @Override
   public <T> Single<T> post(Entity<?> entity, Class<T> clazz)
   {
      return toSingle(publisherInvoker.post(entity, clazz));
   }

   @Override
   public <T> Single<T> post(Entity<?> entity, GenericType<T> type)
   {
      return toSingle(publisherInvoker.post(entity, type));
   }

   @Override
   public Single<Response> delete()
   {
      return toSingle(publisherInvoker.delete());
   }

   @Override
   public <T> Single<T> delete(Class<T> responseType)
   {
      return toSingle(publisherInvoker.delete(responseType));
   }

   @Override
   public <T> Single<T> delete(GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.delete(responseType));
   }

   @Override
   public Single<Response> head()
   {
      return toSingle(publisherInvoker.head());
   }

   @Override
   public Single<Response> options()
   {
      return toSingle(publisherInvoker.options());
   }

   @Override
   public <T> Single<T> options(Class<T> responseType)
   {
      return toSingle(publisherInvoker.options(responseType));
   }

   @Override
   public <T> Single<T> options(GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.options(responseType));
   }

   @Override
   public Single<Response> trace()
   {
      return toSingle(publisherInvoker.trace());
   }

   @Override
   public <T> Single<T> trace(Class<T> responseType)
   {
      return toSingle(publisherInvoker.trace(responseType));
   }

   @Override
   public <T> Single<T> trace(GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.trace(responseType));
   }

   @Override
   public Single<Response> method(String name)
   {
      return toSingle(publisherInvoker.method(name));
   }

   @Override
   public <T> Single<T> method(String name, Class<T> responseType)
   {
      return toSingle(publisherInvoker.method(name, responseType));
   }

   @Override
   public <T> Single<T> method(String name, GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.method(name, responseType));
   }

   @Override
   public Single<Response> method(String name, Entity<?> entity)
   {
      return toSingle(publisherInvoker.method(name, entity));
   }

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, Class<T> responseType)
   {
      return toSingle(publisherInvoker.method(name, entity, responseType));
   }

   @Override
   public <T> Single<T> method(String name, Entity<?> entity, GenericType<T> responseType)
   {
      return toSingle(publisherInvoker.method(name, entity, responseType));
   }
}
