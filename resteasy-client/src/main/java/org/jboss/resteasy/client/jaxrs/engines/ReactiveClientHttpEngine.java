package org.jboss.resteasy.client.jaxrs.engines;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.reactivestreams.Publisher;

import javax.ws.rs.client.InvocationCallback;

public interface ReactiveClientHttpEngine extends AsyncClientHttpEngine
{

   interface Unit<T> {
      void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError, final Runnable onComplete);
      Publisher<T> get();
      CompletableFuture<T> toFuture();
   }


   /**
    * This is the main bridge from RestEasy to a reactive implementation.
    */
   <T> Unit<T> submitRx(
       ClientInvocation request,
       boolean buffered,
       ResultExtractor<T> extractor);

   @Override
   default <T> CompletableFuture<T> submit(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor) {
      return submitRx(request, buffered, extractor).toFuture();
   }

   @Override
   <T> CompletableFuture<T> submit(ClientInvocation request, boolean buffered, ResultExtractor<T> extractor, ExecutorService executorService);

   //<T> Unit<T> fromCompletionStage(CompletionStage<T> cs);

   <T> Unit<T> just(T t);

   /**
    * How the reactive implementation handles errors.
    */
   <T> Unit<T> error(Exception e);
}
