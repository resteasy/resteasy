package org.jboss.resteasy.rxjava;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import rx.Observable;
import rx.RxReactiveStreams;
import rx.plugins.RxJavaHooks;

@Provider
public class ObservableProvider implements AsyncStreamProvider<Observable<?>>
{

   static
   {
      RxJavaHooks.setOnObservableCreate(new ResteasyContextPropagatingOnObservableCreateAction());
   }

   @Override
   public Publisher<?> toAsyncStream(Observable<?> asyncResponse)
   {
      return RxReactiveStreams.toPublisher(asyncResponse);
   }

}
