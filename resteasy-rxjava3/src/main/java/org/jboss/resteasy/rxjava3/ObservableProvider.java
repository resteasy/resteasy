package org.jboss.resteasy.rxjava3;

import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;

@Provider
public class ObservableProvider implements AsyncStreamProvider<Observable<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Observable<?> asyncResponse)
   {
      return asyncResponse.toFlowable(BackpressureStrategy.BUFFER);
   }

}
