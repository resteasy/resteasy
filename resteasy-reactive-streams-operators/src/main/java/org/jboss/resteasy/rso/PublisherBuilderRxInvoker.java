package org.jboss.resteasy.rso;

import javax.ws.rs.client.RxInvoker;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;

public interface PublisherBuilderRxInvoker extends RxInvoker<Publisher<?>>
{
   BackpressureStrategy getBackpressureStrategy();

   void setBackpressureStrategy(BackpressureStrategy backpressureStrategy);
}
