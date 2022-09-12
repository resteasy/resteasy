package org.jboss.resteasy.reactor;

import jakarta.ws.rs.client.RxInvoker;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public interface FluxRxInvoker extends RxInvoker<Flux<?>>
{
   FluxSink.OverflowStrategy getOverflowStrategy();

   void setOverflowStrategy(FluxSink.OverflowStrategy overflowStrategy);
}
