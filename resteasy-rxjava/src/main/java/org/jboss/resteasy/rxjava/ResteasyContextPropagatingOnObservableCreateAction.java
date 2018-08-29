package org.jboss.resteasy.rxjava;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

/**
  * @deprecated:
  * 
  *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
  *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
  *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
  *    
  *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
  */
@SuppressWarnings("rawtypes")
@Deprecated
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

      ContextCapturerObservable(OnSubscribe<T> source)
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

         OnAssemblyObservableSubscriber(Subscriber<? super T> actual, Map<Class<?>, Object> contextDataMap)
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
