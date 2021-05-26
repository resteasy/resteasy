package org.jboss.resteasy.client.jaxrs.engines;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.reactivestreams.Publisher;

public interface ReactiveClientHttpEngine extends AsyncClientHttpEngine
{

   interface PublisherUnit<T> {
      void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError, final Runnable onComplete);
      Publisher<T> get();
   }

   interface Unit<T, U> {
      void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError, final Runnable onComplete);
      U get();
   }


   /**
    * This is the main bridge from RestEasy to a reactive implementation.
    */
   <T, U> Unit<T, U> submitRx(
       ClientInvocation request,
       boolean buffered,
       ResultExtractor<T> extractor);

   <T> PublisherUnit<T> submitRxMaybeGood(
       ClientInvocation request,
       boolean buffered,
       ResultExtractor<T> extractor);


   <T> ReactiveClientHttpEngine.Unit<T> fromCompletionStage(CompletionStage<T> cs);

   <T> ReactiveClientHttpEngine.Unit<T> just(T t);

   /**
    * How the reactive implementation handles errors.
    */
   <T> ReactiveClientHttpEngine.Unit<T> error(Exception e);
}
