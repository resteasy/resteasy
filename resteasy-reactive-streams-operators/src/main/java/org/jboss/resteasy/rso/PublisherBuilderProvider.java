package org.jboss.resteasy.rso;

import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.reactivestreams.Publisher;

@Provider
public class PublisherBuilderProvider implements AsyncStreamProvider<PublisherBuilder<?>>
{
   @Override
   public Publisher<?> toAsyncStream(PublisherBuilder<?> asyncResponse)
   {
      return asyncResponse.buildRs();
   }
}
