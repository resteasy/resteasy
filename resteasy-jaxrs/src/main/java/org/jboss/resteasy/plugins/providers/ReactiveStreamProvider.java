package org.jboss.resteasy.plugins.providers;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

public class ReactiveStreamProvider implements AsyncStreamProvider<Publisher<?>> {

   @SuppressWarnings("rawtypes")
   @Override
   public Publisher toAsyncStream(Publisher<?> asyncResponse)
   {
      return asyncResponse;
   }

}
