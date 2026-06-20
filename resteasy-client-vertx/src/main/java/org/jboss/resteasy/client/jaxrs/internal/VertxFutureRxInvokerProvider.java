package org.jboss.resteasy.client.jaxrs.internal;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

public class VertxFutureRxInvokerProvider implements RxInvokerProvider<VertxFutureRxInvoker> {

    @Override
    public boolean isProviderFor(Class<?> clazz) {
        return VertxFutureRxInvoker.class.equals(clazz);
    }

    @Override
    public VertxFutureRxInvoker getRxInvoker(SyncInvoker syncInvoker, ExecutorService executorService) {
        return new VertxFutureRxInvokerImpl(syncInvoker);
    }
}
