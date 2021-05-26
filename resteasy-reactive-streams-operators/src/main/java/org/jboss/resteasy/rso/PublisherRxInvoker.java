package org.jboss.resteasy.rso;

import javax.ws.rs.client.RxInvoker;

import org.eclipse.microprofile.reactive.streams.operators.spi.ReactiveStreamsEngine;
import org.reactivestreams.Publisher;

public interface PublisherRxInvoker extends RxInvoker<Publisher<?>>
{
   PublisherRxInvoker reactiveStreamsEngine(ReactiveStreamsEngine engine);
}
