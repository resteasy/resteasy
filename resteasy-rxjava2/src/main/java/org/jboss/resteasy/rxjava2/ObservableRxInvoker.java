package org.jboss.resteasy.rxjava2;

import jakarta.ws.rs.client.RxInvoker;

import io.reactivex.Observable;

public interface ObservableRxInvoker extends RxInvoker<Observable<?>>
{

}
