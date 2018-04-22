package org.jboss.resteasy.rxjava;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

import rx.Observable;
import rx.RxReactiveStreams;
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
@Deprecated
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
