package org.jboss.resteasy.test.response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncResponseProvider;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>
{

   private static class SingleAdaptor<T> extends CompletableFuture<T> 
   {
      private Disposable subscription;

      SingleAdaptor(Single<T> observable)
      {
         this.subscription = observable.subscribe(this::complete, this::completeExceptionally);
      }
      
      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         subscription.dispose();
         return super.cancel(mayInterruptIfRunning);
      }
   }
   
   @Override
   public CompletionStage toCompletionStage(Single<?> asyncResponse)
   {
      return new SingleAdaptor<>(asyncResponse);
   }

}
