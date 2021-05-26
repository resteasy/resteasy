package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class UnitRxInvokerImpl<U> implements UnitRxInvoker<U> {

    private final ClientInvocationBuilder builder;

    public UnitRxInvokerImpl(final ClientInvocationBuilder builder) {
        this.builder = builder;
    }

    protected <T> ReactiveClientHttpEngine.Unit<T, CompletionStage<T>> toUnit(CompletionStage<T> completable) {
        return new CompletionStageUnit<>(completable);
    }

    static class CompletionStageUnit<T> implements ReactiveClientHttpEngine.Unit<T, CompletionStage<T>> {
        private final CompletionStage<T> delegate;

        public CompletionStageUnit(CompletionStage<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public CompletionStage<T> get() {
            return delegate;
        }

        @Override
        public void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError, Runnable onComplete) {
            // I can't recall.. Does onComplete run always?
            delegate.whenComplete((t, err) -> {
                try {
                    if (err != null) {
                        onError.accept(err);
                    } else {
                        onSuccess.accept(t);
                    }
                } finally {
                    onComplete.run();
                }
            });
        }
    }

    private <T> ReactiveClientHttpEngine.Unit<T, U> mkUnit(
        final String method,
        final Entity<?> entity,
        final Function<ClientInvocation, ReactiveClientHttpEngine.Unit<T, U>> mkUnit
    ) {
        ClientInvocation invocation = builder.createClientInvocation(builder.invocation);
        invocation.setMethod(method);
        invocation.setEntity(entity);
        return mkUnit.apply(invocation);
    }

    private ReactiveClientHttpEngine.Unit<Response, U> mkUnit(final String method, final Entity<?> entity) {
        return mkUnit(method, entity, invocation ->
            invocation.<U>reactive()
                .map(ClientInvocation.ReactiveInvocation::submit)
                .orElseThrow(() -> new RuntimeException("Not a reactive engine!"))
                // TODO what is the replacement here?? .orElseGet(() -> toUnit(invocation.submitCF()))
        );
    }

    private <T> ReactiveClientHttpEngine.Unit<T, U> mkUnit(final String method, final Entity<?> entity, final Class<T> responseType) {
        return mkUnit(method, entity, invocation ->
            invocation.<U>reactive()
                .map(r -> r.submit(responseType))
                .orElseThrow(() -> new RuntimeException("Not a reactive engine!"))
                // TODO what is the replacement here?? .orElseGet(() -> toUnit(invocation.submitCF()))
        );
    }

    private <T> ReactiveClientHttpEngine.Unit<T, U> mkUnit(final String method, final Entity<?> entity, final GenericType<T> responseType) {
        return mkUnit(method, entity, invocation ->
            invocation.<U>reactive()
                .map(r -> r.submit(responseType))
                .orElseThrow(() -> new RuntimeException("Not a reactive engine!"))
                // TODO what is the replacement here?? .orElseGet(() -> toUnit(invocation.submitCF(responseType)))
        );
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> get() {
        return mkUnit(HttpMethod.GET, null);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> get(final Class<T> responseType) {
        return mkUnit(HttpMethod.GET, null, responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> get(final GenericType<T> responseType) {
        return mkUnit(HttpMethod.GET, null, responseType);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> put(final Entity<?> entity) {
        return mkUnit(HttpMethod.PUT, entity);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> put(final Entity<?> entity, final Class<T> clazz) {
        return mkUnit(HttpMethod.PUT, entity, clazz);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> put(final Entity<?> entity, final GenericType<T> type) {
        return mkUnit(HttpMethod.PUT, entity, type);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> post(final Entity<?> entity) {
        return mkUnit(HttpMethod.POST, entity);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> post(final Entity<?> entity, final Class<T> clazz) {
        return mkUnit(HttpMethod.POST, entity, clazz);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> post(final Entity<?> entity, final GenericType<T> type) {
        return mkUnit(HttpMethod.POST, entity, type);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> delete() {
        return mkUnit(HttpMethod.DELETE, null);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> delete(final Class<T> responseType) {
        return mkUnit(HttpMethod.DELETE, null, responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> delete(final GenericType<T> responseType) {
        return mkUnit(HttpMethod.DELETE, null, responseType);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> head() {
        return mkUnit(HttpMethod.HEAD, null);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> options() {
        return mkUnit(HttpMethod.OPTIONS, null);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> options(final Class<T> responseType) {
        return mkUnit(HttpMethod.OPTIONS, null, responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> options(final GenericType<T> responseType) {
        return mkUnit(HttpMethod.OPTIONS, null, responseType);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> trace() {
        return method("TRACE");
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> trace(final Class<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> trace(final GenericType<T> responseType) {
        return method("TRACE", responseType);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> method(final String name) {
        return mkUnit(name, null);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> method(final String name, final Class<T> responseType) {
        return mkUnit(name, null, responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> method(final String name, final GenericType<T> responseType) {
        return mkUnit(name, null, responseType);
    }

    @Override
    public ReactiveClientHttpEngine.Unit<Response, U> method(final String name, final Entity<?> entity) {
        return mkUnit(name, entity);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> method(final String name, final Entity<?> entity, final Class<T> responseType) {
        return mkUnit(name, entity, responseType);
    }

    @Override
    public <T> ReactiveClientHttpEngine.Unit<T, U> method(final String name, final Entity<?> entity, final GenericType<T> responseType) {
        return mkUnit(name, entity, responseType);
    }
}
