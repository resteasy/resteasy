package org.jboss.resteasy.rxjava2;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;

@SuppressWarnings("rawtypes")
public class ResteasyContextPropagatingOnFlowableCreateAction implements BiFunction<Flowable, Subscriber, Subscriber>
{

   @SuppressWarnings("unchecked")
   @Override
   public Subscriber apply(Flowable t1, Subscriber t2) throws Exception
   {
      return new ContextCapturerSubscriber<>(t2);
   }

   final static class ContextCapturerSubscriber<T> implements Subscriber<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final Subscriber<T> actual;

      ContextCapturerSubscriber(Subscriber<T> actual)
      {
         this.actual = actual;
      }

      @Override
      public void onError(Throwable e)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onError(e);
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onNext(T t)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onNext(t);
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onComplete()
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onComplete();
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onSubscribe(Subscription d)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onSubscribe(d);
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
}
