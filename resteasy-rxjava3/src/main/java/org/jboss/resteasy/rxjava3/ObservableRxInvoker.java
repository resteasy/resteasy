package org.jboss.resteasy.rxjava3;

import jakarta.ws.rs.client.RxInvoker;

import io.reactivex.rxjava3.core.Observable;

public interface ObservableRxInvoker extends RxInvoker<Observable<?>>
{

}
