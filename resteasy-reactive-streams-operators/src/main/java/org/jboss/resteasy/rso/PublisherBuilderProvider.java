package org.jboss.resteasy.rso;

import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

//import io.reactivex.plugins.RxJavaPlugins;

@Provider
public class PublisherBuilderProvider implements AsyncStreamProvider<PublisherBuilder<?>>
{
   // ??
//   static
//   {
//      RxJavaPlugins.setOnFlowableSubscribe(new ResteasyContextPropagatingOnPublisherBuilderCreateAction());
//   }

   @Override
   public Publisher<?> toAsyncStream(PublisherBuilder<?> asyncResponse)
   {
      return asyncResponse.buildRs();
   }
}
