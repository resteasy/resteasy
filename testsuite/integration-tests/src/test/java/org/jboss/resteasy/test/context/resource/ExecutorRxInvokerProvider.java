package org.jboss.resteasy.test.context.resource;

import java.util.concurrent.ExecutorService;

import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.client.SyncInvoker;

/**
 * Provides the {@link ExecutorRxInvoker} for client proxy methods which return a
 * {@link java.util.concurrent.CompletableFuture}.
 *
 * @since 8.0
 */
public class ExecutorRxInvokerProvider implements RxInvokerProvider<ExecutorRxInvoker> {

    @Override
    public boolean isProviderFor(final Class<?> clazz) {
        return ExecutorRxInvoker.class.equals(clazz);
    }

    @Override
    public ExecutorRxInvoker getRxInvoker(final SyncInvoker syncInvoker, final ExecutorService executorService) {
        return new ExecutorRxInvoker(syncInvoker, executorService);
    }
}
