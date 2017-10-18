package org.jboss.resteasy.rxjava;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

@SuppressWarnings("rawtypes")
public class ResteasyContextPropagatingOnObservableCreateAction implements Func1<OnSubscribe, OnSubscribe>
{

   @SuppressWarnings("unchecked")
   @Override
   public OnSubscribe<?> call(OnSubscribe t)
   {
      return new ContextCapturerObservable<>(t);
   }

   final static class ContextCapturerObservable<T> implements OnSubscribe<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final OnSubscribe<T> source;

      public ContextCapturerObservable(OnSubscribe<T> source)
      {
         this.source = source;
      }

      @Override
      public void call(Subscriber<? super T> t)
      {
         source.call(new OnAssemblyObservableSubscriber<T>(t, contextDataMap));
      }

      static final class OnAssemblyObservableSubscriber<T> extends Subscriber<T>
      {

         final Subscriber<? super T> actual;

         final Map<Class<?>, Object> contextDataMap;

         public OnAssemblyObservableSubscriber(Subscriber<? super T> actual, Map<Class<?>, Object> contextDataMap)
         {
            this.actual = actual;
            this.contextDataMap = contextDataMap;
            actual.add(this);
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
         public void onCompleted()
         {
            ResteasyProviderFactory.pushContextDataMap(contextDataMap);
            actual.onCompleted();
            ResteasyProviderFactory.removeContextDataLevel();
         }
      }
   }

}
