package org.jboss.resteasy.rxjava3;

import jakarta.ws.rs.client.RxInvoker;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public interface FlowableRxInvoker extends RxInvoker<Flowable<?>>
{
   BackpressureStrategy getBackpressureStrategy();

   void setBackpressureStrategy(BackpressureStrategy backpressureStrategy);
}
