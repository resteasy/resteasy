package org.jboss.resteasy.rxjava2;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

@Provider
public class FlowableProvider implements AsyncStreamProvider<Flowable<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Flowable<?> asyncResponse)
   {
      return asyncResponse;
   }

}
