package org.jboss.resteasy.rxjava;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import rx.Single;
import rx.Single.OnSubscribe;
import rx.SingleSubscriber;
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
public class ResteasyContextPropagatingOnSingleCreateAction implements Func1<OnSubscribe, OnSubscribe>
{

   @SuppressWarnings("unchecked")
   @Override
   public OnSubscribe<?> call(OnSubscribe t)
   {
      return new ContextCapturerSingle<>(t);
   }

   final static class ContextCapturerSingle<T> implements Single.OnSubscribe<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final Single.OnSubscribe<T> source;

      public ContextCapturerSingle(Single.OnSubscribe<T> source)
      {
         this.source = source;
      }

      @Override
      public void call(SingleSubscriber<? super T> t)
      {
         source.call(new OnAssemblySingleSubscriber<T>(t, contextDataMap));
      }

      static final class OnAssemblySingleSubscriber<T> extends SingleSubscriber<T>
      {

         final SingleSubscriber<? super T> actual;

         final Map<Class<?>, Object> contextDataMap;

         public OnAssemblySingleSubscriber(SingleSubscriber<? super T> actual, Map<Class<?>, Object> contextDataMap)
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
         public void onSuccess(T t)
         {
            ResteasyProviderFactory.pushContextDataMap(contextDataMap);
            actual.onSuccess(t);
            ResteasyProviderFactory.removeContextDataLevel();
         }
      }
   }

}
