package org.jboss.resteasy.rxjava3;

import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.core.Flowable;

@Provider
public class FlowableProvider implements AsyncStreamProvider<Flowable<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Flowable<?> asyncResponse)
   {
      return asyncResponse;
   }

}
