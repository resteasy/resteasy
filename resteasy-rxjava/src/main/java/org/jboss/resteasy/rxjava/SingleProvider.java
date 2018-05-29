package org.jboss.resteasy.rxjava;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;

import rx.Single;
import rx.Subscription;
import rx.plugins.RxJavaHooks;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
@Provider
public class SingleProvider implements AsyncResponseProvider<Single<?>>, AsyncClientResponseProvider<Single<?>>
{

   static
   {
      RxJavaHooks.setOnSingleCreate(new ResteasyContextPropagatingOnSingleCreateAction());
   }

   private static class SingleAdaptor<T> extends CompletableFuture<T>
   {
      private Subscription subscription;

      public SingleAdaptor(Single<T> single)
      {
         this.subscription = single.subscribe(this::complete, this::completeExceptionally);
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning)
      {
         subscription.unsubscribe();
         return super.cancel(mayInterruptIfRunning);
      }
   }

   @Override
   public CompletionStage<?> toCompletionStage(Single<?> asyncResponse)
   {
      return new SingleAdaptor<>(asyncResponse);
   }

   @Override
   public Single<?> fromCompletionStage(CompletionStage<?> completionStage)
   {
      return Single.from(completionStage.toCompletableFuture());
   }

}
