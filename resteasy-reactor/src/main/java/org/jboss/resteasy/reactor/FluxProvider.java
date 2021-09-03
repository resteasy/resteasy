package org.jboss.resteasy.reactor;

import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

@Provider
public class FluxProvider implements AsyncStreamProvider<Flux<?>>
{
   @Override
   public Publisher<?> toAsyncStream(Flux<?> asyncResponse)
   {
      return asyncResponse;
   }

}
