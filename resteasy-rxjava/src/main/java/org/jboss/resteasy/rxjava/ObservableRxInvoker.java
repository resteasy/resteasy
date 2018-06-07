package org.jboss.resteasy.rxjava;

import javax.ws.rs.client.RxInvoker;

import rx.Observable;

/**
 * @deprecated:
 * 
 *   "RxJava 1.x is now officially end-of-life (EOL). No further developments,
 *    bugfixes, enhancements, javadoc changes or maintenance will be provided by
 *    this project after version 1.3.8." - From https://github.com/ReactiveX/RxJava/releases
 *    
 *    Please upgrade to resteasy-rxjava2 and RxJava 2.x.
 */
@Deprecated
public interface ObservableRxInvoker extends RxInvoker<Observable<?>> 
{

}
