package org.jboss.resteasy.rxjava2;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

@SuppressWarnings("rawtypes")
public class ResteasyContextPropagatingOnSingleCreateAction implements BiFunction<Single, SingleObserver, SingleObserver>
{
   @SuppressWarnings("unchecked")
   @Override
   public SingleObserver apply(Single t1, SingleObserver t2) throws Exception
   {
      return new ContextCapturerObserver<>(t2);
   }

   final static class ContextCapturerObserver<T> implements SingleObserver<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final SingleObserver<T> actual;

      ContextCapturerObserver(SingleObserver<T> actual)
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
      public void onSuccess(T value)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onSuccess(value);
         ResteasyProviderFactory.removeContextDataLevel();
      }

      @Override
      public void onSubscribe(Disposable d)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onSubscribe(d);
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
}
