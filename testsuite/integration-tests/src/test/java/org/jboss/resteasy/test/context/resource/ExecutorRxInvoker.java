package org.jboss.resteasy.test.context.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.SyncInvoker;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

/**
 * An {@link RxInvoker} which invokes the {@link SyncInvoker} on the executor it was created with. Unlike the built-in
 * {@link jakarta.ws.rs.client.CompletionStageRxInvoker}, this uses the executor a client proxy passes to the
 * {@link jakarta.ws.rs.client.RxInvokerProvider}.
 *
 * @since 8.0
 */
@SuppressWarnings("rawtypes")
public class ExecutorRxInvoker implements RxInvoker<CompletableFuture> {
    private final SyncInvoker invoker;
    private final ExecutorService executor;

    ExecutorRxInvoker(final SyncInvoker invoker, final ExecutorService executor) {
        this.invoker = invoker;
        this.executor = executor;
    }

    @Override
    public CompletableFuture<Response> get() {
        return supply(invoker::get);
    }

    @Override
    public <R> CompletableFuture<R> get(final Class<R> responseType) {
        return supply(() -> invoker.get(responseType));
    }

    @Override
    public <R> CompletableFuture<R> get(final GenericType<R> responseType) {
        return supply(() -> invoker.get(responseType));
    }

    @Override
    public CompletableFuture<Response> put(final Entity<?> entity) {
        return supply(() -> invoker.put(entity));
    }

    @Override
    public <R> CompletableFuture<R> put(final Entity<?> entity, final Class<R> responseType) {
        return supply(() -> invoker.put(entity, responseType));
    }

    @Override
    public <R> CompletableFuture<R> put(final Entity<?> entity, final GenericType<R> responseType) {
        return supply(() -> invoker.put(entity, responseType));
    }

    @Override
    public CompletableFuture<Response> post(final Entity<?> entity) {
        return supply(() -> invoker.post(entity));
    }

    @Override
    public <R> CompletableFuture<R> post(final Entity<?> entity, final Class<R> responseType) {
        return supply(() -> invoker.post(entity, responseType));
    }

    @Override
    public <R> CompletableFuture<R> post(final Entity<?> entity, final GenericType<R> responseType) {
        return supply(() -> invoker.post(entity, responseType));
    }

    @Override
    public CompletableFuture<Response> delete() {
        return supply(invoker::delete);
    }

    @Override
    public <R> CompletableFuture<R> delete(final Class<R> responseType) {
        return supply(() -> invoker.delete(responseType));
    }

    @Override
    public <R> CompletableFuture<R> delete(final GenericType<R> responseType) {
        return supply(() -> invoker.delete(responseType));
    }

    @Override
    public CompletableFuture<Response> head() {
        return supply(invoker::head);
    }

    @Override
    public CompletableFuture<Response> options() {
        return supply(invoker::options);
    }

    @Override
    public <R> CompletableFuture<R> options(final Class<R> responseType) {
        return supply(() -> invoker.options(responseType));
    }

    @Override
    public <R> CompletableFuture<R> options(final GenericType<R> responseType) {
        return supply(() -> invoker.options(responseType));
    }

    @Override
    public CompletableFuture<Response> trace() {
        return supply(invoker::trace);
    }

    @Override
    public <R> CompletableFuture<R> trace(final Class<R> responseType) {
        return supply(() -> invoker.trace(responseType));
    }

    @Override
    public <R> CompletableFuture<R> trace(final GenericType<R> responseType) {
        return supply(() -> invoker.trace(responseType));
    }

    @Override
    public CompletableFuture<Response> method(final String name) {
        return supply(() -> invoker.method(name));
    }

    @Override
    public <R> CompletableFuture<R> method(final String name, final Class<R> responseType) {
        return supply(() -> invoker.method(name, responseType));
    }

    @Override
    public <R> CompletableFuture<R> method(final String name, final GenericType<R> responseType) {
        return supply(() -> invoker.method(name, responseType));
    }

    @Override
    public CompletableFuture<Response> method(final String name, final Entity<?> entity) {
        return supply(() -> invoker.method(name, entity));
    }

    @Override
    public <R> CompletableFuture<R> method(final String name, final Entity<?> entity, final Class<R> responseType) {
        return supply(() -> invoker.method(name, entity, responseType));
    }

    @Override
    public <R> CompletableFuture<R> method(final String name, final Entity<?> entity,
            final GenericType<R> responseType) {
        return supply(() -> invoker.method(name, entity, responseType));
    }

    private <R> CompletableFuture<R> supply(final Supplier<R> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }
}
