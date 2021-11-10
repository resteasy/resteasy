package org.jboss.resteasy.rxjava2;

import jakarta.ws.rs.client.RxInvoker;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public interface FlowableRxInvoker extends RxInvoker<Flowable<?>>
{
   BackpressureStrategy getBackpressureStrategy();

   void setBackpressureStrategy(BackpressureStrategy backpressureStrategy);
}
