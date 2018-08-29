package org.jboss.resteasy.rxjava2;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;

@Provider
public class ObservableProvider implements AsyncStreamProvider<Observable<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Observable<?> asyncResponse)
   {
      return asyncResponse.toFlowable(BackpressureStrategy.BUFFER);
   }

}
