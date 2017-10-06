package org.jboss.resteasy.rxjava2;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.plugins.RxJavaPlugins;

@Provider
public class FlowableProvider implements AsyncStreamProvider<Flowable<?>>
{

   static
   {
      RxJavaPlugins.setOnFlowableSubscribe(new ResteasyContextPropagatingOnFlowableCreateAction());
   }

   @Override
   public Publisher<?> toAsyncStream(Flowable<?> asyncResponse)
   {
      return asyncResponse;
   }

}
