package org.jboss.resteasy.rxjava2;

import java.util.Map;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

@SuppressWarnings("rawtypes")
public class ResteasyContextPropagatingOnObservableCreateAction implements BiFunction<Observable, Observer, Observer>
{

   @SuppressWarnings("unchecked")
   @Override
   public Observer apply(Observable t1, Observer t2) throws Exception
   {
      return new ContextCapturerObserver<>(t2);
   }

   final static class ContextCapturerObserver<T> implements Observer<T>
   {

      final Map<Class<?>, Object> contextDataMap = ResteasyProviderFactory.getContextDataMap();

      final Observer<T> actual;

      ContextCapturerObserver(Observer<T> actual)
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
      public void onSubscribe(Disposable d)
      {
         ResteasyProviderFactory.pushContextDataMap(contextDataMap);
         actual.onSubscribe(d);
         ResteasyProviderFactory.removeContextDataLevel();
      }
   }
}
