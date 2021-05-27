package org.jboss.resteasy.reactor;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.engines.ReactiveClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.UnitRxInvoker;
import org.jboss.resteasy.client.jaxrs.internal.UnitRxInvokerImpl;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

public class MonoRxInvokerImpl implements MonoRxInvoker {

    private final UnitRxInvoker unitRxInvoker;

    public MonoRxInvokerImpl(final UnitRxInvoker unitRxInvoker) {
        this.unitRxInvoker = unitRxInvoker;
    }

    class MMono<T> extends Mono<T> {
        private final ReactiveClientHttpEngine.Unit<T> delegate;

        public MMono(ReactiveClientHttpEngine.Unit<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void subscribe(CoreSubscriber coreSubscriber) {
            delegate.subscribe(
                coreSubscriber::onNext,
                coreSubscriber::onError,
                coreSubscriber::onComplete
            );
        }
    }

    @Override
    public Mono<Response> get() {
        return new MMono(unitRxInvoker.get());
    }

    @Override
    public <T> Mono<T> get(Class<T> responseType) {
        return new MMono(unitRxInvoker.get(responseType));
    }

    @Override
    public <T> Mono<T> get(GenericType<T> responseType) {
        return new MMono(unitRxInvoker.get(responseType));
    }

    @Override
    public Mono<Response> put(Entity<?> entity) {
        return new MMono(unitRxInvoker.put(entity));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, Class<T> clazz) {
        return new MMono(unitRxInvoker.put(entity, clazz));
    }

    @Override
    public <T> Mono<T> put(Entity<?> entity, GenericType<T> type) {
        return new MMono(unitRxInvoker.put(entity, type));
    }

    @Override
    public Mono<Response> post(Entity<?> entity) {
        return new MMono(unitRxInvoker.post(entity));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, Class<T> clazz) {
        return new MMono(unitRxInvoker.post(entity, clazz));
    }

    @Override
    public <T> Mono<T> post(Entity<?> entity, GenericType<T> type) {
        return new MMono(unitRxInvoker.post(entity, type));
    }

    @Override
    public Mono<Response> delete() {
        return new MMono(unitRxInvoker.delete());
    }

    @Override
    public <T> Mono<T> delete(Class<T> responseType) {
        return new MMono(unitRxInvoker.delete(responseType));
    }

    @Override
    public <T> Mono<T> delete(GenericType<T> responseType) {
        return new MMono(unitRxInvoker.delete(responseType));
    }

    @Override
    public Mono<Response> head() {
        return new MMono(unitRxInvoker.head());
    }

    @Override
    public Mono<Response> options() {
        return new MMono(unitRxInvoker.options());
    }

    @Override
    public <T> Mono<T> options(Class<T> responseType) {
        return new MMono(unitRxInvoker.options(responseType));
    }

    @Override
    public <T> Mono<T> options(GenericType<T> responseType) {
        return new MMono(unitRxInvoker.options(responseType));
    }

    @Override
    public Mono<Response> trace() {
        return new MMono(unitRxInvoker.trace());
    }

    @Override
    public <T> Mono<T> trace(Class<T> responseType) {
        return new MMono(unitRxInvoker.trace(responseType));
    }

    @Override
    public <T> Mono<T> trace(GenericType<T> responseType) {
        return new MMono(unitRxInvoker.trace(responseType));
    }

    @Override
    public Mono<Response> method(String name) {
        return new MMono(unitRxInvoker.method(name));
    }

    @Override
    public <T> Mono<T> method(String name, Class<T> responseType) {
        return new MMono(unitRxInvoker.method(name, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, GenericType<T> responseType) {
        return new MMono(unitRxInvoker.method(name, responseType));
    }

    @Override
    public Mono<Response> method(String name, Entity<?> entity) {
        return new MMono(unitRxInvoker.method(name, entity));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, Class<T> responseType) {
        return new MMono(unitRxInvoker.method(name, entity, responseType));
    }

    @Override
    public <T> Mono<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
        return new MMono(unitRxInvoker.method(name, entity, responseType));
    }
}
